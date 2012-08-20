package com.javaboz.commons.test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InvalidClassException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamClass;
import java.io.OutputStream;
import java.util.LinkedList;
import java.util.Queue;

/**
 * Deep clone processor.<br>
 * Use serialization process to deep copy object.
 *
 * @author Julien Boz
 */
public final class CloneUtils {

	private CloneUtils() {
	}

	public static <T> T clone(final T x) {
		try {
			return cloneX(x);
		} catch (final IOException e) {
			throw new IllegalArgumentException(e);
		} catch (final ClassNotFoundException e) {
			throw new IllegalArgumentException(e);
		}
	}

	@SuppressWarnings("unchecked")
	private static <T> T cloneX(final T x) throws IOException, ClassNotFoundException {
		final ByteArrayOutputStream bout = new ByteArrayOutputStream();
		final CloneOutput cout = new CloneOutput(bout);
		cout.writeObject(x);
		final byte[] bytes = bout.toByteArray();

		final ByteArrayInputStream bin = new ByteArrayInputStream(bytes);

		final CloneInput cin = new CloneInput(bin, cout);
		try {
			return (T) cin.readObject();
		} finally {
			cin.close();
		}
	}

	private static class CloneOutput extends ObjectOutputStream {
		final Queue<Class<?>> classQueue = new LinkedList<Class<?>>();

		CloneOutput(final OutputStream out) throws IOException {
			super(out);
		}

		@Override
		protected void annotateClass(final Class<?> c) {
			classQueue.add(c);
		}

		@Override
		protected void annotateProxyClass(final Class<?> c) {
			classQueue.add(c);
		}
	}

	private static class CloneInput extends ObjectInputStream {
		private final CloneOutput output;

		CloneInput(final InputStream in, final CloneOutput output) throws IOException {
			super(in);
			this.output = output;
		}

		@Override
		protected Class<?> resolveClass(final ObjectStreamClass osc) throws IOException, ClassNotFoundException {
			final Class<?> c = output.classQueue.poll();
			final String expected = osc.getName();
			final String found = c == null ? null : c.getName();
			if (!expected.equals(found)) {
				throw new InvalidClassException("Classes desynchronized: " + "found " + found + " when expecting " + expected);
			}
			return c;
		}

		@Override
		protected Class<?> resolveProxyClass(final String[] interfaceNames) throws IOException, ClassNotFoundException {
			return output.classQueue.poll();
		}
	}
}