/* -*- Mode: java; tab-width: 8; indent-tabs-mode: nil; c-basic-offset: 4 -*-
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package com.skoow.rhino;

/**
 * Map to associate non-negative integers to objects or integers.
 * The map does not synchronize any of its operation, so either use
 * it from a single thread or do own synchronization or perform all mutation
 * operations on one thread before passing the map to others.
 *
 * @author Igor Bukanov
 */
@SuppressWarnings("unused")
public class UintMap {
	// Map implementation via hashtable,
	// follows "The Art of Computer Programming" by Donald E. Knuth

	// A == golden_ratio * (1 << 32) = ((sqrt(5) - 1) / 2) * (1 << 32)
	// See Knuth etc.
	private static final int A = 0x9e3779b9;
	private static final int EMPTY = -1;
	private static final int DELETED = -2;
	// If true, enables consitency checks
	private static final boolean check = false;

	private static int tableLookupStep(int fraction, int mask, int power) {
		int shift = 32 - 2 * power;
		if (shift >= 0) {
			return ((fraction >>> shift) & mask) | 1;
		}
		return (fraction & (mask >>> -shift)) | 1;
	}


	private transient int[] keys;
	private transient Object[] values;
	private int power;
	private int keyCount;
	private transient int occupiedCount; // == keyCount + deleted_count
	// If ivaluesShift != 0, keys[ivaluesShift + index] contains integer
	// values associated with keys
	private transient int ivaluesShift;

	public UintMap() {
		this(4);
	}

	public UintMap(int initialCapacity) {
		if (initialCapacity < 0) {
			Kit.codeBug();
		}
		// Table grow when number of stored keys >= 3/4 of max capacity
		int minimalCapacity = initialCapacity * 4 / 3;
		int i;
		for (i = 2; (1 << i) < minimalCapacity; ++i) {
		}
		power = i;
		if (check && power < 2) {
			Kit.codeBug();
		}
	}

	public boolean isEmpty() {
		return keyCount == 0;
	}

	public int size() {
		return keyCount;
	}

	public boolean has(int key) {
		if (key < 0) {
			Kit.codeBug();
		}
		return 0 <= findIndex(key);
	}

	/**
	 * Get object value assigned with key.
	 *
	 * @return key object value or null if key is absent
	 */
	public Object getObject(int key) {
		if (key < 0) {
			Kit.codeBug();
		}
		if (values != null) {
			int index = findIndex(key);
			if (0 <= index) {
				return values[index];
			}
		}
		return null;
	}

	/**
	 * Get integer value assigned with key.
	 *
	 * @return key integer value or defaultValue if key is absent
	 */
	public int getInt(int key, int defaultValue) {
		if (key < 0) {
			Kit.codeBug();
		}
		int index = findIndex(key);
		if (0 <= index) {
			if (ivaluesShift != 0) {
				return keys[ivaluesShift + index];
			}
			return 0;
		}
		return defaultValue;
	}

	/**
	 * Get integer value assigned with key.
	 *
	 * @return key integer value or defaultValue if key does not exist or does
	 * not have int value
	 * @throws RuntimeException if key does not exist
	 */
	public int getExistingInt(int key) {
		if (key < 0) {
			Kit.codeBug();
		}
		int index = findIndex(key);
		if (0 <= index) {
			if (ivaluesShift != 0) {
				return keys[ivaluesShift + index];
			}
			return 0;
		}
		// Key must exist
		Kit.codeBug();
		return 0;
	}

	/**
	 * Set object value of the key.
	 * If key does not exist, also set its int value to 0.
	 */
	public void put(int key, Object value) {
		if (key < 0) {
			Kit.codeBug();
		}
		int index = ensureIndex(key, false);
		if (values == null) {
			values = new Object[1 << power];
		}
		values[index] = value;
	}

	/**
	 * Set int value of the key.
	 * If key does not exist, also set its object value to null.
	 */
	public void put(int key, int value) {
		if (key < 0) {
			Kit.codeBug();
		}
		int index = ensureIndex(key, true);
		if (ivaluesShift == 0) {
			int N = 1 << power;
			// keys.length can be N * 2 after clear which set ivaluesShift to 0
			if (keys.length != N * 2) {
				int[] tmp = new int[N * 2];
				System.arraycopy(keys, 0, tmp, 0, N);
				keys = tmp;
			}
			ivaluesShift = N;
		}
		keys[ivaluesShift + index] = value;
	}

	// Structure of kyes and values arrays (N == 1 << power):
	// keys[0 <= i < N]: key value or EMPTY or DELETED mark
	// values[0 <= i < N]: value of key at keys[i]
	// keys[N <= i < 2N]: int values of keys at keys[i - N]

	public void remove(int key) {
		if (key < 0) {
			Kit.codeBug();
		}
		int index = findIndex(key);
		if (0 <= index) {
			keys[index] = DELETED;
			--keyCount;
			// Allow to GC value and make sure that new key with the deleted
			// slot shall get proper default values
			if (values != null) {
				values[index] = null;
			}
			if (ivaluesShift != 0) {
				keys[ivaluesShift + index] = 0;
			}
		}
	}

	public void clear() {
		int N = 1 << power;
		if (keys != null) {
			for (int i = 0; i != N; ++i) {
				keys[i] = EMPTY;
			}
			if (values != null) {
				for (int i = 0; i != N; ++i) {
					values[i] = null;
				}
			}
		}
		ivaluesShift = 0;
		keyCount = 0;
		occupiedCount = 0;
	}

	/**
	 * Return array of present keys
	 */
	public int[] getKeys() {
		int[] keys = this.keys;
		int n = keyCount;
		int[] result = new int[n];
		for (int i = 0; n != 0; ++i) {
			int entry = keys[i];
			if (entry != EMPTY && entry != DELETED) {
				result[--n] = entry;
			}
		}
		return result;
	}

	private int findIndex(int key) {
		int[] keys = this.keys;
		if (keys != null) {
			int fraction = key * A;
			int index = fraction >>> (32 - power);
			int entry = keys[index];
			if (entry == key) {
				return index;
			}
			if (entry != EMPTY) {
				// Search in table after first failed attempt
				int mask = (1 << power) - 1;
				int step = tableLookupStep(fraction, mask, power);
				int n = 0;
				do {
					if (check) {
						if (n >= occupiedCount) {
							Kit.codeBug();
						}
						++n;
					}
					index = (index + step) & mask;
					entry = keys[index];
					if (entry == key) {
						return index;
					}
				} while (entry != EMPTY);
			}
		}
		return -1;
	}

	// Insert key that is not present to table without deleted entries
	// and enough free space
	private int insertNewKey(int key) {
		if (check && occupiedCount != keyCount) {
			Kit.codeBug();
		}
		if (check && keyCount == 1 << power) {
			Kit.codeBug();
		}
		int[] keys = this.keys;
		int fraction = key * A;
		int index = fraction >>> (32 - power);
		if (keys[index] != EMPTY) {
			int mask = (1 << power) - 1;
			int step = tableLookupStep(fraction, mask, power);
			int firstIndex = index;
			do {
				if (check && keys[index] == DELETED) {
					Kit.codeBug();
				}
				index = (index + step) & mask;
				if (check && firstIndex == index) {
					Kit.codeBug();
				}
			} while (keys[index] != EMPTY);
		}
		keys[index] = key;
		++occupiedCount;
		++keyCount;
		return index;
	}

	private void rehashTable(boolean ensureIntSpace) {
		if (keys != null) {
			// Check if removing deleted entries would free enough space
			if (keyCount * 2 >= occupiedCount) {
				// Need to grow: less then half of deleted entries
				++power;
			}
		}
		int N = 1 << power;
		int[] old = keys;
		int oldShift = ivaluesShift;
		if (oldShift == 0 && !ensureIntSpace) {
			keys = new int[N];
		} else {
			ivaluesShift = N;
			keys = new int[N * 2];
		}
		for (int i = 0; i != N; ++i) {
			keys[i] = EMPTY;
		}

		Object[] oldValues = values;
		if (oldValues != null) {
			values = new Object[N];
		}

		int oldCount = keyCount;
		occupiedCount = 0;
		if (oldCount != 0) {
			keyCount = 0;
			for (int i = 0, remaining = oldCount; remaining != 0; ++i) {
				int key = old[i];
				if (key != EMPTY && key != DELETED) {
					int index = insertNewKey(key);
					if (oldValues != null) {
						values[index] = oldValues[i];
					}
					if (oldShift != 0) {
						keys[ivaluesShift + index] = old[oldShift + i];
					}
					--remaining;
				}
			}
		}
	}

	// Ensure key index creating one if necessary
	private int ensureIndex(int key, boolean intType) {
		int index = -1;
		int firstDeleted = -1;
		int[] keys = this.keys;
		if (keys != null) {
			int fraction = key * A;
			index = fraction >>> (32 - power);
			int entry = keys[index];
			if (entry == key) {
				return index;
			}
			if (entry != EMPTY) {
				if (entry == DELETED) {
					firstDeleted = index;
				}
				// Search in table after first failed attempt
				int mask = (1 << power) - 1;
				int step = tableLookupStep(fraction, mask, power);
				int n = 0;
				do {
					if (check) {
						if (n >= occupiedCount) {
							Kit.codeBug();
						}
						++n;
					}
					index = (index + step) & mask;
					entry = keys[index];
					if (entry == key) {
						return index;
					}
					if (entry == DELETED && firstDeleted < 0) {
						firstDeleted = index;
					}
				} while (entry != EMPTY);
			}
		}
		// Inserting of new key
		if (check && keys != null && keys[index] != EMPTY) {
			Kit.codeBug();
		}
		if (firstDeleted >= 0) {
			index = firstDeleted;
		} else {
			// Need to consume empty entry: check occupation level
			if (keys == null || occupiedCount * 4 >= (1 << power) * 3) {
				// Too litle unused entries: rehash
				rehashTable(intType);
				return insertNewKey(key);
			}
			++occupiedCount;
		}
		keys[index] = key;
		++keyCount;
		return index;
	}
}
