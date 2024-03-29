/**
 * Copyright (c) 2010-11 The AEminium Project (see AUTHORS file)
 * 
 * This file is part of Plaid Programming Language.
 *
 * Plaid Programming Language is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 *  Plaid Programming Language is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with Plaid Programming Language.  If not, see <http://www.gnu.org/licenses/>.
 */

package aeminium.runtime.benchmarks.fjtests.forkjoin;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import aeminium.runtime.Body;
import aeminium.runtime.Runtime;
import aeminium.runtime.Task;

import jsr166y.ForkJoinPool;

public class LogCounter {
	
	public static void main(String[] args) throws Exception {
		//TODO
		/*
		long start, end;
		
		cleanFiles(path);
		System.out.println("AE :::::::::");
		aeminium.runtime.Runtime rt = Factory.getRuntime();
		
		
		start = System.nanoTime();
		System.out.println("n=" + aeminiumCounter(path, rt));
		end = System.nanoTime();
		System.out.println(String.format("%d", (end-start)));
		*/
		
	}

	public static void cleanFiles(String path) throws IOException,
			InterruptedException {
		Process p;
		p = java.lang.Runtime.getRuntime().exec("python " + path + "restore.py");
		p.waitFor();
	}
	
	public static int sequentialCounter(File[] files) {
		int n = 0;
		for (File logfile : files) {
			String d;
			try {
				d = uncompressGZip(logfile);
			} catch (IOException e) {
				e.printStackTrace();
				continue;
			}
			
			try {
				n += countAccesses(d);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
		}
		return n;
	}

	private static int countAccesses(String d)
			throws FileNotFoundException, IOException { 
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader(d));
			int count = 0;
			String line;
			while (true) {
				line = reader.readLine();
				//System.out.println(line);
				if (line == null) break;
				if (line.startsWith("wiki.alcidesfonseca.com")) {
					count++;
				}
				return count;
			}
		} finally {
			if ( reader != null ) {
				reader.close();
			}
		}
		return 0;
	}
	
	public static File compressGZip(File source) throws IOException {
		if (source.getAbsolutePath().contains(".gz")) return source;
		
		String dest = source.getAbsolutePath() + ".gz";
		File d = new File(dest);
		InputStream in = new FileInputStream(source);
		OutputStream out = new GZIPOutputStream(new FileOutputStream(d));
		try {
			byte[] buffer = new byte[65536];
			int noRead;
			while ((noRead = in.read(buffer)) != -1) {
				out.write(buffer, 0, noRead);
			}
		} finally {
			try {
				out.close();
				in.close();
			} catch (Exception e) {
			}
		}
		return d;
	}

	private static String uncompressGZip(File source) throws IOException {
		if (!source.getAbsolutePath().contains(".gz")) return source.getAbsolutePath();
		
		String dest = source.getAbsolutePath().replace(".gz", "");
		File d = new File(dest);
		InputStream in = new FileInputStream(source);
		OutputStream out = new FileOutputStream(d);
		try {
			in = new GZIPInputStream(in);
			byte[] buffer = new byte[65536];
			int noRead;
			while ((noRead = in.read(buffer)) != -1) {
				out.write(buffer, 0, noRead);
			}
		} finally {
			try {
				out.close();
				in.close();
			} catch (Exception e) {
			}
		}
		return dest;
	}
	
	
	static class FJCounter implements Callable<Integer> {

		private static final long serialVersionUID = 2879359934394010878L;

		File f;

		public FJCounter(File f) {
			this.f = f;
		}
		
		@Override
		public Integer call() {
			int result = 0;
			String d;
			try {
				d = uncompressGZip(f);
			} catch (IOException e) {
				e.printStackTrace();
				return 0;
			}
			
			try {
				result = countAccesses(d);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			return result;
		}
	}
	
	public static int forkjoinCounter(File[] files, ForkJoinPool pool) {
		int n = 0;
		Collection<FJCounter> futures = new ArrayList<FJCounter>();
		
		for (File logfile : files) {
			futures.add(new FJCounter(logfile));
		}
		List<Future<Integer>> results = pool.invokeAll(futures);
		
		for (Future<Integer> result : results) {
			try {
				if (result.get() == null) {
					System.out.println("null!");
				}
				n += result.get();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ExecutionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		return n;
	}

	
	public static int aeminiumCounter(File[] files, aeminium.runtime.Runtime rt) {
		rt.init();
		final ArrayList<Task> counterTasks = new ArrayList<Task>();
		
		for (final File logfile : files) {
			final Task uncompress = rt.createBlockingTask(new Body() {
		
				@Override
				public void execute(Runtime rt, Task current) {
					try {
						current.setResult(uncompressGZip(logfile));
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
		
			}, aeminium.runtime.Runtime.NO_HINTS);
			rt.schedule(uncompress, Runtime.NO_PARENT, Runtime.NO_DEPS);
			
			Task count = rt.createBlockingTask(new Body() {
				
				@Override
				public void execute(Runtime rt, Task current) {
					try {
						current.setResult(countAccesses((String) uncompress.getResult()));
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
		
			}, aeminium.runtime.Runtime.NO_HINTS);
			rt.schedule(count, Runtime.NO_PARENT, Arrays.asList(uncompress));
			counterTasks.add(count);
		}
		
		Task merge = rt.createBlockingTask(new Body() {
			
			@Override
			public void execute(Runtime rt, Task current) {
				int n = 0;
				for (Task t : counterTasks) {
					Integer r = (Integer) t.getResult();
					if (r != null) {
						n += r;
					}
				}
				current.setResult(n);
				
			}
	
		}, aeminium.runtime.Runtime.NO_HINTS);
		rt.schedule(merge, Runtime.NO_PARENT, counterTasks);
		
		rt.shutdown();
		return (Integer) merge.getResult();
	}
	
}
