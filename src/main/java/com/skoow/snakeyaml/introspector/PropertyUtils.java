/**
 * Copyright (c) 2008, SnakeYAML
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package com.skoow.snakeyaml.introspector;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import com.skoow.snakeyaml.error.YAMLException;
import com.skoow.snakeyaml.introspector.BeanAccess;
import com.skoow.snakeyaml.introspector.FieldProperty;
import com.skoow.snakeyaml.introspector.Property;
import com.skoow.snakeyaml.util.PlatformFeatureDetector;

public class PropertyUtils {

  private final Map<Class<?>, Map<String, com.skoow.snakeyaml.introspector.Property>> propertiesCache =
      new HashMap<Class<?>, Map<String, com.skoow.snakeyaml.introspector.Property>>();
  private final Map<Class<?>, Set<com.skoow.snakeyaml.introspector.Property>> readableProperties =
      new HashMap<Class<?>, Set<com.skoow.snakeyaml.introspector.Property>>();
  private com.skoow.snakeyaml.introspector.BeanAccess beanAccess = com.skoow.snakeyaml.introspector.BeanAccess.DEFAULT;
  private boolean allowReadOnlyProperties = false;
  private boolean skipMissingProperties = false;

  private final PlatformFeatureDetector platformFeatureDetector;

  public PropertyUtils() {
    this(new PlatformFeatureDetector());
  }

  PropertyUtils(PlatformFeatureDetector platformFeatureDetector) {
    this.platformFeatureDetector = platformFeatureDetector;
    if (!platformFeatureDetector.isIntrospectionAvailable()) {
      beanAccess = com.skoow.snakeyaml.introspector.BeanAccess.FIELD;
    }
  }

  protected Map<String, com.skoow.snakeyaml.introspector.Property> getPropertiesMap(Class<?> type, com.skoow.snakeyaml.introspector.BeanAccess bAccess) {
    if (propertiesCache.containsKey(type)) {
      return propertiesCache.get(type);
    }

    Map<String, com.skoow.snakeyaml.introspector.Property> properties = new LinkedHashMap<String, com.skoow.snakeyaml.introspector.Property>();
    boolean inaccessableFieldsExist = false;
    if (bAccess == com.skoow.snakeyaml.introspector.BeanAccess.FIELD) {
      for (Class<?> c = type; c != null; c = c.getSuperclass()) {
        for (Field field : c.getDeclaredFields()) {
          int modifiers = field.getModifiers();
          if (!Modifier.isStatic(modifiers) && !Modifier.isTransient(modifiers)
              && !properties.containsKey(field.getName())) {
            properties.put(field.getName(), new FieldProperty(field));
          }
        }
      }
    } else {// add JavaBean properties
      inaccessableFieldsExist = MethodProperty.addPublicFields(type, properties);
    }
    if (properties.isEmpty() && inaccessableFieldsExist) {
      throw new YAMLException("No JavaBean properties found in " + type.getName());
    }
    propertiesCache.put(type, properties);
    return properties;
  }

  public Set<com.skoow.snakeyaml.introspector.Property> getProperties(Class<? extends Object> type) {
    return getProperties(type, beanAccess);
  }

  public Set<com.skoow.snakeyaml.introspector.Property> getProperties(Class<? extends Object> type, com.skoow.snakeyaml.introspector.BeanAccess bAccess) {
    if (readableProperties.containsKey(type)) {
      return readableProperties.get(type);
    }
    Set<com.skoow.snakeyaml.introspector.Property> properties = createPropertySet(type, bAccess);
    readableProperties.put(type, properties);
    return properties;
  }

  protected Set<com.skoow.snakeyaml.introspector.Property> createPropertySet(Class<? extends Object> type, com.skoow.snakeyaml.introspector.BeanAccess bAccess) {
    Set<com.skoow.snakeyaml.introspector.Property> properties = new TreeSet<com.skoow.snakeyaml.introspector.Property>();
    Collection<com.skoow.snakeyaml.introspector.Property> props = getPropertiesMap(type, bAccess).values();
    for (com.skoow.snakeyaml.introspector.Property property : props) {
      if (property.isReadable() && (allowReadOnlyProperties || property.isWritable())) {
        properties.add(property);
      }
    }
    return properties;
  }

  public com.skoow.snakeyaml.introspector.Property getProperty(Class<? extends Object> type, String name) {
    return getProperty(type, name, beanAccess);
  }

  public com.skoow.snakeyaml.introspector.Property getProperty(Class<? extends Object> type, String name, com.skoow.snakeyaml.introspector.BeanAccess bAccess) {
    Map<String, com.skoow.snakeyaml.introspector.Property> properties = getPropertiesMap(type, bAccess);
    Property property = properties.get(name);
    if (property == null && skipMissingProperties) {
      property = new MissingProperty(name);
    }
    if (property == null) {
      throw new YAMLException("Unable to find property '" + name + "' on class: " + type.getName());
    }
    return property;
  }

  public void setBeanAccess(com.skoow.snakeyaml.introspector.BeanAccess beanAccess) {
    if (platformFeatureDetector.isRunningOnAndroid() && beanAccess != BeanAccess.FIELD) {
      throw new IllegalArgumentException("JVM is Android - only BeanAccess.FIELD is available");
    }

    if (this.beanAccess != beanAccess) {
      this.beanAccess = beanAccess;
      propertiesCache.clear();
      readableProperties.clear();
    }
  }

  public void setAllowReadOnlyProperties(boolean allowReadOnlyProperties) {
    if (this.allowReadOnlyProperties != allowReadOnlyProperties) {
      this.allowReadOnlyProperties = allowReadOnlyProperties;
      readableProperties.clear();
    }
  }

  public boolean isAllowReadOnlyProperties() {
    return allowReadOnlyProperties;
  }

  /**
   * Skip properties that are missing during deserialization of YAML to a Java object. The default
   * is false.
   *
   * @param skipMissingProperties true if missing properties should be skipped, false otherwise.
   */
  public void setSkipMissingProperties(boolean skipMissingProperties) {
    if (this.skipMissingProperties != skipMissingProperties) {
      this.skipMissingProperties = skipMissingProperties;
      readableProperties.clear();
    }
  }

  public boolean isSkipMissingProperties() {
    return skipMissingProperties;
  }
}
