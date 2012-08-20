package com.javaboz.commons.test;

import static org.fest.assertions.Assertions.assertThat;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.junit.Test;

/**
 * Test {@link CloneUtils}.
 *
 * @author Julien Boz
 */
public class CloneUtilsTest {

	@Test
	public void testCloneT() {
		final Hom hom = new Hom("Jack");
		hom.addChild("Bob");
		hom.addChild("Jack Junior");
		hom.setWif("Margaret");

		final Hom previousLife = CloneUtils.clone(hom);

		assertThat(previousLife).isNotNull();
		assertThat(previousLife).isNotSameAs(hom); // not same instance
		assertThat(previousLife.name).isEqualTo(hom.name);
		assertThat(previousLife.childs).isEqualTo(hom.childs);
		assertThat(previousLife.wif).isEqualTo(hom.wif);

		// après divorce
		hom.setWif("Betty");
		// et un accident malheureux
		hom.clearChilds();

		// not change previous life
		assertThat(previousLife.name).isEqualTo("Jack");
		assertThat(previousLife.childs).isNotSameAs(hom.childs);
		assertThat(previousLife.childs).hasSize(2).containsExactly(new Person("Bob"), new Person("Jack Junior"));
		assertThat(previousLife.wif).isEqualTo(new Person("Margaret"));
	}

	private static class Hom extends Person {
		private static final long serialVersionUID = 1L;

		private List<Person> childs;
		private Person wif;

		public Hom(final String name) {
			super(name);
		}

		void addChild(final String name) {
			if (childs == null) {
				childs = new ArrayList<Person>();
			}
			childs.add(new Person(name));
		}

		void clearChilds() {
			if (childs != null) {
				childs.clear();
			}
		}

		public void setWif(final String name) {
			wif = new Person(name);
		}

		@Override
		public boolean equals(final Object obj) {
			return EqualsBuilder.reflectionEquals(this, obj);
		}
	}

	private static class Person implements Serializable {
		private static final long serialVersionUID = 1L;

		final String name;

		public Person(final String name) {
			this.name = name;
		}

		@Override
		public boolean equals(final Object obj) {
			return super.equals(obj) || obj instanceof Person && ((Person) obj).name.equals(name);
		}

		@Override
		public int hashCode() {
			// TODO Auto-generated method stub
			return name.hashCode();
		}
	}
}
