package com.github.flycat.module;


public enum ClassNameFilter {

		PRESENT {

			@Override
			public boolean matches(String className, ClassLoader classLoader) {
				return isPresent(className, classLoader);
			}

		},

		MISSING {

			@Override
			public boolean matches(String className, ClassLoader classLoader) {
				return !isPresent(className, classLoader);
			}

		};

		public abstract boolean matches(String className, ClassLoader classLoader);

		public static boolean isPresent(String className, ClassLoader classLoader) {
			if (classLoader == null) {
				classLoader = Thread.currentThread().getContextClassLoader();
			}
			try {
				forName(className, classLoader);
				return true;
			}
			catch (Throwable ex) {
				return false;
			}
		}

		private static Class<?> forName(String className, ClassLoader classLoader) throws ClassNotFoundException {
			if (classLoader != null) {
				return classLoader.loadClass(className);
			}
			return Class.forName(className);
		}

	}
