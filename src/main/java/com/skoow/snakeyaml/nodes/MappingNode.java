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
package com.skoow.snakeyaml.nodes;

import java.util.List;
import com.skoow.snakeyaml.DumperOptions;
import com.skoow.snakeyaml.error.Mark;
import com.skoow.snakeyaml.nodes.NodeId;
import com.skoow.snakeyaml.nodes.NodeTuple;
import com.skoow.snakeyaml.nodes.Tag;

/**
 * Represents a map.
 * <p>
 * A map is a collection of unsorted key-value pairs.
 * </p>
 */
public class MappingNode extends CollectionNode<com.skoow.snakeyaml.nodes.NodeTuple> {

  private List<com.skoow.snakeyaml.nodes.NodeTuple> value;
  private boolean merged = false;

  public MappingNode(com.skoow.snakeyaml.nodes.Tag tag, boolean resolved, List<com.skoow.snakeyaml.nodes.NodeTuple> value, Mark startMark, Mark endMark,
                     DumperOptions.FlowStyle flowStyle) {
    super(tag, startMark, endMark, flowStyle);
    if (value == null) {
      throw new NullPointerException("value in a Node is required.");
    }
    this.value = value;
    this.resolved = resolved;
  }

  public MappingNode(Tag tag, List<com.skoow.snakeyaml.nodes.NodeTuple> value, DumperOptions.FlowStyle flowStyle) {
    this(tag, true, value, null, null, flowStyle);
  }

  @Override
  public com.skoow.snakeyaml.nodes.NodeId getNodeId() {
    return NodeId.mapping;
  }

  /**
   * Returns the entries of this map.
   *
   * @return List of entries.
   */
  public List<com.skoow.snakeyaml.nodes.NodeTuple> getValue() {
    return value;
  }

  public void setValue(List<com.skoow.snakeyaml.nodes.NodeTuple> mergedValue) {
    value = mergedValue;
  }

  public void setOnlyKeyType(Class<? extends Object> keyType) {
    for (com.skoow.snakeyaml.nodes.NodeTuple nodes : value) {
      nodes.getKeyNode().setType(keyType);
    }
  }

  public void setTypes(Class<? extends Object> keyType, Class<? extends Object> valueType) {
    for (com.skoow.snakeyaml.nodes.NodeTuple nodes : value) {
      nodes.getValueNode().setType(valueType);
      nodes.getKeyNode().setType(keyType);
    }
  }

  @Override
  public String toString() {
    String values;
    StringBuilder buf = new StringBuilder();
    for (NodeTuple node : getValue()) {
      buf.append("{ key=");
      buf.append(node.getKeyNode());
      buf.append("; value=");
      if (node.getValueNode() instanceof CollectionNode) {
        // to avoid overflow in case of recursive structures
        buf.append(System.identityHashCode(node.getValueNode()));
      } else {
        buf.append(node);
      }
      buf.append(" }");
    }
    values = buf.toString();
    return "<" + this.getClass().getName() + " (tag=" + getTag() + ", values=" + values + ")>";
  }

  /**
   * @param merged - true if map contains merge node
   */
  public void setMerged(boolean merged) {
    this.merged = merged;
  }

  /**
   * @return true if map contains merge node
   */
  public boolean isMerged() {
    return merged;
  }
}
