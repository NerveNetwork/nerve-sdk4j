/**
 * MIT License
 * <p>
 * Copyright (c) 2017-2018 nuls.io
 * <p>
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * <p>
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * <p>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package network.nerve.anybus;

import network.nerve.anybus.AnyBusEvent;
import network.nerve.anybus.ProgramTransfer;
import network.nerve.base.basic.NulsByteBuffer;
import network.nerve.base.basic.NulsOutputStreamBuffer;
import network.nerve.base.data.BaseNulsData;
import network.nerve.core.exception.NulsException;
import network.nerve.core.parse.SerializeUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Contract call result with NULS serialization support
 *
 * @author: PierreLuo
 * @date: 2025/9/24
 */
public class AnyBusCallResult extends BaseNulsData {

    /**
     * String - Call result (can be null)
     */
    private String result;

    /**
     * SHA256 hash digest for all mutations (PUT and DELETE operations).
     */
    private String dbRoot;

    /**
     * List<ProgramTransfer> - Transfer list
     */
    private List<ProgramTransfer> transfers;

    /**
     * List<AnyBusEvent> - Event list
     */
    private List<AnyBusEvent> events;

    public AnyBusCallResult() {
        this.transfers = new ArrayList<>();
        this.events = new ArrayList<>();
    }

    @Override
    protected void serializeToStream(NulsOutputStreamBuffer stream) throws IOException {
        // 1. Serialize result (String, nullable)
        stream.writeString(result);
        stream.writeString(dbRoot);

        // 2. Serialize transfers list (List<ProgramTransfer>)
        // Reference: BlockHeader.blockSignature uses writeNulsData for nested BaseNulsData
        if (transfers == null || transfers.isEmpty()) {
            stream.writeVarInt(0);
        } else {
            stream.writeVarInt(transfers.size());
            for (ProgramTransfer transfer : transfers) {
                stream.writeNulsData(transfer);
            }
        }

        // 3. Serialize events list (List<AnyBusEvent>)
        if (events == null || events.isEmpty()) {
            stream.writeVarInt(0);
        } else {
            stream.writeVarInt(events.size());
            for (AnyBusEvent event : events) {
                stream.writeNulsData(event);
            }
        }
    }

    @Override
    public void parse(NulsByteBuffer byteBuffer) throws NulsException {
        // 1. Parse result
        this.result = byteBuffer.readString();
        this.dbRoot = byteBuffer.readString();

        // 2. Parse transfers list
        // Reference: BlockHeader.blockSignature uses readNulsData for nested BaseNulsData
        long transfersCount = byteBuffer.readVarInt();
        this.transfers = new ArrayList<>((int) transfersCount);
        for (int i = 0; i < transfersCount; i++) {
            ProgramTransfer transfer = byteBuffer.readNulsData(new ProgramTransfer());
            this.transfers.add(transfer);
        }

        // 3. Parse events list
        long eventsCount = byteBuffer.readVarInt();
        this.events = new ArrayList<>((int) eventsCount);
        for (int i = 0; i < eventsCount; i++) {
            AnyBusEvent event = byteBuffer.readNulsData(new AnyBusEvent());
            this.events.add(event);
        }
    }

    @Override
    public int size() {
        int size = 0;

        // 1. Result (String)
        size += SerializeUtils.sizeOfString(result);
        size += SerializeUtils.sizeOfString(dbRoot);

        // 2. Transfers list
        // Reference: BlockHeader uses sizeOfNulsData for nested BaseNulsData
        size += SerializeUtils.sizeOfVarInt(transfers == null ? 0 : transfers.size());
        if (transfers != null) {
            for (ProgramTransfer transfer : transfers) {
                size += SerializeUtils.sizeOfNulsData(transfer);
            }
        }

        // 3. Events list
        size += SerializeUtils.sizeOfVarInt(events == null ? 0 : events.size());
        if (events != null) {
            for (AnyBusEvent event : events) {
                size += SerializeUtils.sizeOfNulsData(event);
            }
        }

        return size;
    }

    // Getters and Setters

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getDbRoot() {
        return dbRoot;
    }

    public void setDbRoot(String dbRoot) {
        this.dbRoot = dbRoot;
    }

    public List<ProgramTransfer> getTransfers() {
        return transfers;
    }

    public void setTransfers(List<ProgramTransfer> transfers) {
        this.transfers = transfers != null ? transfers : new ArrayList<>();
    }

    public List<AnyBusEvent> getEvents() {
        return events;
    }

    public void setEvents(List<AnyBusEvent> events) {
        this.events = events != null ? events : new ArrayList<>();
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("{");
        sb.append("\"result\":");
        sb.append('\"').append(result == null ? "" : result).append('\"');
        sb.append("\"dbRoot\":");
        sb.append('\"').append(dbRoot == null ? "" : dbRoot).append('\"');
        sb.append(",\"transfers\":")
                .append(transfers);
        sb.append(",\"events\":")
                .append(events);
        sb.append('}');
        return sb.toString();
    }
}
