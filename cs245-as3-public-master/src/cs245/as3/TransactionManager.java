package cs245.as3;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import cs245.as3.interfaces.LogManager;
import cs245.as3.interfaces.StorageManager;
import cs245.as3.interfaces.StorageManager.TaggedValue;

/**
 * You will implement this class.
 *
 * The implementation we have provided below performs atomic transactions but the changes are not durable.
 * Feel free to replace any of the data structures in your implementation, though the instructor solution includes
 * the same data structures (with additional fields) and uses the same strategy of buffering writes until commit.
 *
 * Your implementation need not be threadsafe, i.e. no methods of TransactionManager are ever called concurrently.
 *
 * You can assume that the constructor and initAndRecover() are both called before any of the other methods.
 */
public class TransactionManager {
	private LogManager lm=null;
	private StorageManager sm=null;
	long logk=Long.MAX_VALUE;
	long logtag=Long.MAX_VALUE;
	int logl=-1;
	private HashMap<Long, TaggedValue> lass=new HashMap<>();
	class WritesetEntry {
		public long key;
		public byte[] value;
		public WritesetEntry(long key, byte[] value) {
			this.key = key;
			this.value = value;
		}
	}
	/**
	 * Holds the latest value for each key.
	 */
	private HashMap<Long, TaggedValue> latestValues;
	/**
	 * Hold on to writesets until commit.
	 */
	private HashMap<Long, ArrayList<WritesetEntry>> writesets;

	public TransactionManager() {
		writesets = new HashMap<>();
		//see initAndRecover
		latestValues = null;
	}

	/**
	 * Prepare the transaction manager to serve operations.
	 * At this time you should detect whether the StorageManager is inconsistent and recover it.
	 */
	public void initAndRecover(StorageManager sm, LogManager lm) {
		this.lm=lm;
		this.sm=sm;
		latestValues = sm.readStoredTable();
		int offset=this.lm.getLogTruncationOffset();
		while(offset<this.lm.getLogEndOffset()){
			byte[] record=this.lm.readLogRecord(offset,20);
			ByteBuffer wrap=ByteBuffer.wrap(record,0,8);
			long key=wrap.getLong();
			ByteBuffer wrap1=ByteBuffer.wrap(record,8,8);
			long tag=wrap1.getLong();
			ByteBuffer wrap2=ByteBuffer.wrap(record,16,4);
			int l=wrap2.getInt();
			offset+=20;
			record=this.lm.readLogRecord(offset,l);
			ByteBuffer wrap3=ByteBuffer.wrap(record,0,l);
			byte[] value=new byte[l];
			wrap3.get(value);
			offset+=l;
			latestValues.put(key,new TaggedValue(tag,value));
			sm.queueWrite(key,tag,value);
		}
		this.lm.setLogTruncationOffset(offset);
	}

	/**
	 * Indicates the start of a new transaction. We will guarantee that txID always increases (even across crashes)
	 */
	public void start(long txID) {
		// TODO: Not implemented for non-durable transactions, you should implement this
	}

	/**
	 * Returns the latest committed value for a key by any transaction.
	 */
	public byte[] read(long txID, long key) {
		TaggedValue taggedValue = latestValues.get(key);
		return taggedValue == null ? null : taggedValue.value;
	}

	/**
	 * Indicates a write to the database. Note that such writes should not be visible to read()
	 * calls until the transaction making the write commits. For simplicity, we will not make reads
	 * to this same key from txID itself after we make a write to the key.
	 */
	public void write(long txID, long key, byte[] value) {
		ArrayList<WritesetEntry> writeset = writesets.get(txID);
		if (writeset == null) {
			writeset = new ArrayList<>();
			writesets.put(txID, writeset);
		}
		writeset.add(new WritesetEntry(key, value));
	}
	/**
	 * Commits a transaction, and makes its writes visible to subsequent read operations.\
	 */
	public void commit(long txID) {
		ArrayList<WritesetEntry> writeset = writesets.get(txID);
		if (writeset != null) {
			for(WritesetEntry x : writeset) {
				//tag is unused in this implementation:
				long tag = latestValues.getOrDefault(x.key,new TaggedValue(0,null)).tag+1;
				latestValues.put(x.key, new TaggedValue(tag, x.value));
				ByteBuffer ret=ByteBuffer.allocate(Long.BYTES+Long.BYTES+Integer.BYTES+x.value.length);
				ret.putLong(x.key);
				ret.putLong(tag);
				ret.putInt(x.value.length);
				ret.put(x.value);
				this.lm.appendLogRecord(ret.array());
				sm.queueWrite(x.key,tag,x.value);
			}
			writesets.remove(txID);
		}
	}
	/**
	 * Aborts a transaction.
	 */
	public void abort(long txID) {
		writesets.remove(txID);
	}

	/**
	 * The storage manager will call back into this procedure every time a queued write becomes persistent.
	 * These calls are in order of writes to a key and will occur once for every such queued write, unless a crash occurs.
	 */
	public void writePersisted(long key, long persisted_tag, byte[] persisted_value) {
		lass.put(key,new TaggedValue(persisted_tag,persisted_value));
		if(logk==Long.MAX_VALUE&&logtag==Long.MAX_VALUE){
			int offset=this.lm.getLogTruncationOffset();
			if(offset!=this.lm.getLogEndOffset()){
				byte[] record=this.lm.readLogRecord(offset,20);
				ByteBuffer wrap=ByteBuffer.wrap(record,0,8);
				logk=wrap.getLong();
				ByteBuffer wrap1=ByteBuffer.wrap(record,8,8);
				logtag=wrap1.getLong();
				ByteBuffer wrap2=ByteBuffer.wrap(record,16,4);
				logl=wrap2.getInt();
			}
		}
		persisted_tag = lass.getOrDefault(logk, new TaggedValue(0,null)).tag;
		while(persisted_tag>=logtag) {
			if(this.lm.getLogTruncationOffset() + 20+logl>=this.lm.getLogEndOffset()) break;
			this.lm.setLogTruncationOffset(this.lm.getLogTruncationOffset() + 20+logl);
			int offset = this.lm.getLogTruncationOffset();
			byte[] record = this.lm.readLogRecord(offset, 20);
			ByteBuffer wrap = ByteBuffer.wrap(record, 0, 8);
			logk = wrap.getLong();
			ByteBuffer wrap1 = ByteBuffer.wrap(record, 8, 8);
			logtag = wrap1.getLong();
			persisted_tag = lass.getOrDefault(logk, new TaggedValue(0,null)).tag;
			ByteBuffer wrap2=ByteBuffer.wrap(record,16,4);
			logl=wrap2.getInt();
		}
	}
}
