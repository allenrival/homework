package cs245.as3;

import java.nio.ByteBuffer;
import java.util.*;

import cs245.as3.driver.LogManagerImpl;
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
	private long logk=-1;
	private long logtag=-1;
	private int logl=-1;
	private final HashMap<Long, TaggedValue> lass=new HashMap<>();
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
		latestValues = this.sm.readStoredTable();
		HashMap<Long,TaggedValue>store=new HashMap<>();
		int offset=this.lm.getLogTruncationOffset();
		boolean flag=true;
		while(offset<this.lm.getLogEndOffset()){
			byte[] record=this.lm.readLogRecord(offset,Math.min(128,this.lm.getLogEndOffset()-offset));
			ByteBuffer wrap=ByteBuffer.wrap(record);
			long key=wrap.getLong();
			long tag=wrap.getLong();
			int l=wrap.getInt();
			offset+=20;
			byte[] value=new byte[l];
			wrap.get(value);
			offset+=l;
			if(flag&&key!=-1&&key!=-2){
				store.put(key,new TaggedValue(tag,value));
			}else if(flag&&key==-1){
				for(Map.Entry<Long,TaggedValue> entry:store.entrySet()){
					TaggedValue t=entry.getValue();
					latestValues.put(entry.getKey(),new TaggedValue(t.tag,t.value));
					this.sm.queueWrite(entry.getKey(),t.tag,t.value);
				}
				store.clear();
				flag=false;
			}else if(key==-2) {
				flag=true;
				store.clear();
			}
		}
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
		HashMap<Long,TaggedValue> store=new HashMap<>();
		ArrayList<WritesetEntry> writeset = writesets.get(txID);
		if (writeset != null) {
			ByteBuffer ret = ByteBuffer.allocate(20+"start".getBytes().length);
			ret.putLong(-2);
			ret.putLong(-2);
			ret.putInt("start".getBytes().length);
			ret.put("start".getBytes());
			lm.appendLogRecord(ret.array());
			for (WritesetEntry x : writeset) {
				//tag is unused in this implementation:
				long tag = latestValues.getOrDefault(x.key, new TaggedValue(0, null)).tag + 1;
				ret = ByteBuffer.allocate(20+ x.value.length);
				ret.putLong(x.key);
				ret.putLong(tag);
				ret.putInt(x.value.length);
				ret.put(x.value);
				lm.appendLogRecord(ret.array());
				store.put(x.key,new TaggedValue(tag, x.value));
			}
			ret = ByteBuffer.allocate(20+"commit".getBytes().length);
			ret.putLong(-1L);
			ret.putLong(-1L);
			ret.putInt("commit".getBytes().length);
			ret.put("commit".getBytes());
			lm.appendLogRecord(ret.array());
			for(Map.Entry<Long,TaggedValue> entry:store.entrySet()){
				TaggedValue t=entry.getValue();
				latestValues.put(entry.getKey(),new TaggedValue(t.tag,t.value));
				this.sm.queueWrite(entry.getKey(),t.tag,t.value);
			}
		}
		writesets.remove(txID);
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
		int offset=lm.getLogTruncationOffset();
		while(offset<lm.getLogEndOffset()){
			if(logl==-1){
				byte[] record=this.lm.readLogRecord(offset,20);
				ByteBuffer wrap=ByteBuffer.wrap(record);
				logk=wrap.getLong();
				logtag=wrap.getLong();
				logl=wrap.getInt();
			}
			offset+=20+logl;
			persisted_tag = lass.getOrDefault(logk, new TaggedValue(0,null)).tag;
			if(persisted_tag>=logtag){
				lm.setLogTruncationOffset(offset);
				logl=-1;
			}else break;

		}
	}
}
