package trikita.kv;

import java.util.ArrayList;
import java.util.List;

public final class KV {

	public interface Storage {
		public void set(String key, byte[] value);
		public byte[] get(String key);
		public List<String> keys(String mask);
	}

	public interface Transform {
		public byte[] encode(String key, byte[] input);
		public byte[] decode(String key, byte[] input);
	}

	public interface Encoder {
		public <T> byte[] encode(String key, T value);
		public <T> T decode(String key, byte[] data);
	}

	private Storage mStorage;
	private Transform[] mTransforms;
	private Encoder mEncoder;

	public KV(Storage storage, Encoder enc, Transform ...t) {
		mTransforms = t;
		mEncoder = enc;
		mStorage = storage;
	}

	public <T> KV set(String key, T value) {
		if (value == null) {
			mStorage.set(key, null);
			return this;
		}
		byte[] data = mEncoder.encode(key, value);
		for (Transform t : mTransforms) {
			data = t.encode(key, data);
		}
		mStorage.set(key, data);
		return this;
	}

	public <T> T get(String key) {
		byte[] data = mStorage.get(key);
		if (data == null) {
			return null;
		}
		for (Transform t : mTransforms) {
			data = t.decode(key, data);
		}
		return mEncoder.decode(key, data);
	}

	public List<String> keys(String mask) {
		return mStorage.keys(mask);
	}

	// TODO rlock(), lock(), unlock, runlock()
	// TODO storage sharedprefs
	// TODO storage sqlite
	// TODO storage with lru cache
	// TODO transform for gzip
	// TODO transform for base64
	// TODO transform for aes?
	// TODO encoder for jackson
	// TODO encoder for serializable
}
