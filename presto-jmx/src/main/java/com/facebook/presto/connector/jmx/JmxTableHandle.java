/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.facebook.presto.connector.jmx;

import com.facebook.presto.spi.ConnectorTableHandle;
import com.facebook.presto.spi.ConnectorTableMetadata;
import com.facebook.presto.spi.SchemaTableName;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.ImmutableList;

import java.util.List;
import java.util.Objects;

import static com.google.common.base.MoreObjects.toStringHelper;
import static com.google.common.collect.Iterables.transform;
import static java.util.Objects.requireNonNull;

public class JmxTableHandle
        implements ConnectorTableHandle
{
    private final String connectorId;
    private final String objectName;
    private final List<JmxColumnHandle> columnHandles;
    private final boolean liveData;

    @Deprecated
    public JmxTableHandle(String connectorId, String objectName, List<JmxColumnHandle> columnHandles)
    {
        this(connectorId, objectName, columnHandles, true);
    }

    @JsonCreator
    public JmxTableHandle(
            @JsonProperty("connectorId") String connectorId,
            @JsonProperty("objectName") String objectName,
            @JsonProperty("columnHandles") List<JmxColumnHandle> columnHandles,
            @JsonProperty("liveData") boolean liveData)
    {
        this.connectorId = requireNonNull(connectorId, "connectorId is null");
        this.objectName = requireNonNull(objectName, "objectName is null");
        this.columnHandles = ImmutableList.copyOf(requireNonNull(columnHandles, "columnHandles is null"));
        this.liveData = liveData;
    }

    @JsonProperty
    public String getConnectorId()
    {
        return connectorId;
    }

    @JsonProperty
    public String getObjectName()
    {
        return objectName;
    }

    @JsonProperty
    public List<JmxColumnHandle> getColumnHandles()
    {
        return columnHandles;
    }

    @JsonProperty
    public boolean isLiveData()
    {
        return liveData;
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(connectorId, objectName, columnHandles, liveData);
    }

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        JmxTableHandle other = (JmxTableHandle) obj;
        return Objects.equals(this.connectorId, other.connectorId) &&
                Objects.equals(this.objectName, other.objectName) &&
                Objects.equals(this.columnHandles, other.columnHandles) &&
                Objects.equals(this.liveData, other.liveData);
    }

    @Override
    public String toString()
    {
        return toStringHelper(this)
                .add("connectorId", connectorId)
                .add("objectName", objectName)
                .add("columnHandles", columnHandles)
                .add("liveData", liveData)
                .toString();
    }

    public ConnectorTableMetadata getTableMetadata()
    {
        return new ConnectorTableMetadata(
                new SchemaTableName(JmxMetadata.JMX_SCHEMA_NAME, objectName),
                ImmutableList.copyOf(transform(columnHandles, JmxColumnHandle::getColumnMetadata)));
    }
}
