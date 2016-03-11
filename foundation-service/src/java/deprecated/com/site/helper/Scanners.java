package com.site.helper;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class Scanners {
	public static DirScanner forDir() {
		return DirScanner.INSTANCE;
	}

	public static JarScanner forJar() {
		return JarScanner.INSTANCE;
	}

	public static abstract class DirMatcher implements IMatcher<File> {
		@Override
		public boolean isDirEligible() {
			return true;
		}

		@Override
		public boolean isFileElegible() {
			return false;
		}
	}

	public enum DirScanner {
		INSTANCE;

		public List<File> scan(File base, IMatcher<File> matcher) {
			List<File> files = new ArrayList<File>();
			StringBuilder relativePath = new StringBuilder();

			scanForFiles(base, relativePath, matcher, false, files);

			return files;
		}

		private void scanForFiles(File base, StringBuilder relativePath, IMatcher<File> matcher, boolean foundFirst,
				List<File> files) {
			int len = relativePath.length();
			File dir = len == 0 ? base : new File(base, relativePath.toString());
			String[] list = dir.list();

			if (list != null) {
				for (String item : list) {
					File child = new File(dir, item);

					if (len > 0) {
						relativePath.append('/');
					}

					relativePath.append(item);

					if (matcher.isDirEligible() && child.isDirectory()) {
						IMatcher.Direction direction = matcher.matches(base, relativePath.toString());

						switch (direction) {
						case MATCHED:
							files.add(child);
							break;
						case DOWN:
							// for sub-folders
							scanForFiles(base, relativePath, matcher, foundFirst, files);
							break;
						}
					} else if (matcher.isFileElegible()) {
						IMatcher.Direction direction = matcher.matches(base, relativePath.toString());

						switch (direction) {
						case MATCHED:
							if (child.isFile()) {
								files.add(child);
							}
							break;
						case DOWN:
							if (child.isDirectory()) {
								// for sub-folders
								scanForFiles(base, relativePath, matcher, foundFirst, files);
							}
							break;
						}
					}

					relativePath.setLength(len); // reset

					if (foundFirst && files.size() > 0) {
						break;
					}
				}
			}
		}

		public File scanForOne(File base, IMatcher<File> matcher) {
			List<File> files = new ArrayList<File>(1);
			StringBuilder relativePath = new StringBuilder();

			scanForFiles(base, relativePath, matcher, true, files);

			if (files.isEmpty()) {
				return null;
			} else {
				return files.get(0);
			}
		}
	}

	public static abstract class FileMatcher implements IMatcher<File> {
		@Override
		public boolean isDirEligible() {
			return false;
		}

		@Override
		public boolean isFileElegible() {
			return true;
		}
	}

	public static interface IMatcher<T> {
		public boolean isDirEligible();

		public boolean isFileElegible();

		public Direction matches(T base, String path);

		public enum Direction {
			MATCHED,

			DOWN,

			NEXT;

			public boolean isDown() {
				return this == DOWN;
			}

			public boolean isMatched() {
				return this == MATCHED;
			}

			public boolean isNext() {
				return this == NEXT;
			}
		}
	}

	public enum JarScanner {
		INSTANCE;

		public ZipEntry getEntry(String jarFileName, String name) {
			ZipFile zipFile = null;

			try {
				zipFile = new ZipFile(jarFileName);

				ZipEntry entry = zipFile.getEntry(name);

				return entry;
			} catch (IOException e1) {
				// ignore
			} finally {
				if (zipFile != null) {
					try {
						zipFile.close();
					} catch (IOException e) {
						// ignore it
					}
				}
			}

			return null;
		}

		public byte[] getEntryContent(String jarFileName, String entryPath) {
			byte[] bytes = null;
			ZipFile zipFile = null;

			try {
				zipFile = new ZipFile(jarFileName);
				ZipEntry entry = zipFile.getEntry(entryPath);

				if (entry != null) {
					InputStream inputStream = zipFile.getInputStream(entry);
					bytes = Files.forIO().readFrom(inputStream);
				}
			} catch (Exception e) {
				// ignore
			} finally {
				if (zipFile != null) {
					try {
						zipFile.close();
					} catch (Exception e) {
					}
				}
			}

			return bytes;
		}

		public boolean hasEntry(String jarFileName, String name) {
			return getEntry(jarFileName, name) != null;
		}

		public List<String> scan(File base, IMatcher<File> matcher) {
			List<String> files = new ArrayList<String>();
			scanForFiles(base, matcher, false, files);

			return files;
		}

		private void scanForFiles(File jarFile, IMatcher<File> matcher, boolean foundFirst, List<String> names) {
			ZipFile zipFile = null;

			try {
				zipFile = new ZipFile(jarFile);
			} catch (IOException e) {
				// ignore it
			}

			if (zipFile != null) {
				Enumeration<? extends ZipEntry> entries = zipFile.entries();

				while (entries.hasMoreElements()) {
					ZipEntry entry = entries.nextElement();
					String name = entry.getName();

					if (matcher.isDirEligible() && entry.isDirectory()) {
						IMatcher.Direction direction = matcher.matches(jarFile, name);

						if (direction.isMatched()) {
							names.add(name);
						}
					} else if (matcher.isFileElegible() && !entry.isDirectory()) {
						IMatcher.Direction direction = matcher.matches(jarFile, name);

						if (direction.isMatched()) {
							names.add(name);
						}
					}

					if (foundFirst && names.size() > 0) {
						break;
					}
				}
			}
		}

		public String scanForOne(File jarFile, IMatcher<File> matcher) {
			List<String> files = new ArrayList<String>(1);

			scanForFiles(jarFile, matcher, true, files);

			if (files.isEmpty()) {
				return null;
			} else {
				return files.get(0);
			}
		}
	}
}
