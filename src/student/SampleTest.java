package student;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.Vector;

import org.junit.Test;
import org.junit.Before;
import org.junit.After;
import java.io.*;

public class SampleTest {

	String PATH = resolveSampleDataPath();

	private static String resolveSampleDataPath() {
		String[] candidates = {
			".",
			"SpellAssign2026Framework"
		};

		for (String candidate : candidates) {
			File dir = new File(candidate);
			if (new File(dir, "sample_P1.in").isFile()) {
				return dir.getPath() + File.separator;
			}
		}

		throw new IllegalStateException("Could not find sample input files from " + new File(".").getAbsolutePath());
	}
	

	/*
	 * Sample PASS level tests
	 */

	@Test
	public void testP1_5() {
		BuildSpellbook d = new BuildSpellbook();
		Vector<String> inSpecs = null;
		Vector<String> soln = null;
		Vector<String> execd = null;
		String datfile = "sample_P1";
		Integer N = 5;

		try {
			inSpecs = d.readSpecsFromFile(PATH+datfile+".in");
		}
		catch (IOException e) {
			System.out.println("in exception: " + e);
		}
		
		execd = d.executeNSpecs(inSpecs, N);
		
		try {
			soln = d.readSolnFromFile(PATH+datfile+".out", N);
		}
		catch (IOException e) {
			System.out.println("in exception: " + e);
		}
		
		assertTrue(d.compareExecutedWSoln(execd, soln));
		
	}

	@Test
	public void testP1_9() {
		BuildSpellbook d = new BuildSpellbook();
		Vector<String> inSpecs = null;
		Vector<String> soln = null;
		Vector<String> execd = null;
		String datfile = "sample_P1";
		Integer N = 9;

		try {
			inSpecs = d.readSpecsFromFile(PATH+datfile+".in");
		}
		catch (IOException e) {
			System.out.println("in exception: " + e);
		}
		
		execd = d.executeNSpecs(inSpecs, N);
		
		try {
			soln = d.readSolnFromFile(PATH+datfile+".out", N);
		}
		catch (IOException e) {
			System.out.println("in exception: " + e);
		}
		
		assertTrue(d.compareExecutedWSoln(execd, soln));
		
	}
	
	@Test
	public void testP1_all() {
		BuildSpellbook d = new BuildSpellbook();
		Vector<String> inSpecs = null;
		Vector<String> soln = null;
		Vector<String> execd = null;
		String datfile = "sample_P1";
		Integer N = d.MAXCOMS;

		try {
			inSpecs = d.readSpecsFromFile(PATH+datfile+".in");
		}
		catch (IOException e) {
			System.out.println("in exception: " + e);
		}
		
		execd = d.executeNSpecs(inSpecs, N);
		
		try {
			soln = d.readSolnFromFile(PATH+datfile+".out", N);
		}
		catch (IOException e) {
			System.out.println("in exception: " + e);
		}
		
		assertTrue(d.compareExecutedWSoln(execd, soln));
		
	}

	@Test
	public void testP2_all() {
		BuildSpellbook d = new BuildSpellbook();
		Vector<String> inSpecs = null;
		Vector<String> soln = null;
		Vector<String> execd = null;
		String datfile = "sample_P2";
		Integer N = d.MAXCOMS;

		try {
			inSpecs = d.readSpecsFromFile(PATH+datfile+".in");
		}
		catch (IOException e) {
			System.out.println("in exception: " + e);
		}
		
		execd = d.executeNSpecs(inSpecs, N);
		
		try {
			soln = d.readSolnFromFile(PATH+datfile+".out", N);
		}
		catch (IOException e) {
			System.out.println("in exception: " + e);
		}
		
		assertTrue(d.compareExecutedWSoln(execd, soln));
		
	}

	/*
	 * Sample CREDIT level tests 
	 */
	
	@Test
	public void testC1_6() {
		BuildSpellbook d = new BuildSpellbook();
		Vector<String> inSpecs = null;
		Vector<String> soln = null;
		Vector<String> execd = null;
		String datfile = "sample_C1";
		Integer N = 6;

		try {
			inSpecs = d.readSpecsFromFile(PATH+datfile+".in");
		}
		catch (IOException e) {
			System.out.println("in exception: " + e);
		}
		
		execd = d.executeNSpecswCheck(inSpecs, N);
		
		try {
			soln = d.readSolnFromFile(PATH+datfile+".out", N);
		}
		catch (IOException e) {
			System.out.println("in exception: " + e);
		}
		
		assertTrue(d.compareExecutedWSoln(execd, soln));
		
	}

	@Test
	public void testC1_all() {
		BuildSpellbook d = new BuildSpellbook();
		Vector<String> inSpecs = null;
		Vector<String> soln = null;
		Vector<String> execd = null;
		String datfile = "sample_C1";
		Integer N = d.MAXCOMS;

		try {
			inSpecs = d.readSpecsFromFile(PATH+datfile+".in");
		}
		catch (IOException e) {
			System.out.println("in exception: " + e);
		}
		
		execd = d.executeNSpecswCheck(inSpecs, N);
		
		try {
			soln = d.readSolnFromFile(PATH+datfile+".out", N);
		}
		catch (IOException e) {
			System.out.println("in exception: " + e);
		}
		
		assertTrue(d.compareExecutedWSoln(execd, soln));
		
	}

	/*
	 * Sample (HIGH) DISTINCTION level tests 
	 */


	@Test
	public void testD1_all() {
		BuildSpellbook d = new BuildSpellbook();
		Vector<String> inSpecs = null;
		Vector<String> soln = null;
		Vector<String> execd = null;
		String datfile = "sample_D1";
		Integer N = d.MAXCOMS;

		try {
			inSpecs = d.readSpecsFromFile(PATH+datfile+".in");
		}
		catch (IOException e) {
			System.out.println("in exception: " + e);
		}
		
		execd = d.executeNSpecswCheckRecLarge(inSpecs, N);
		
		try {
			soln = d.readSolnFromFile(PATH+datfile+".out", N);
		}
		catch (IOException e) {
			System.out.println("in exception: " + e);
		}
		
		assertTrue(d.compareExecutedWSoln(execd, soln));
		
	}

	
	//	public void testD1a_8() {
	
	@Test
	public void testD1a_8() {
		BuildSpellbook d = new BuildSpellbook();
		Vector<String> inSpecs = null;
		Vector<String> soln = null;
		Vector<String> execd = null;
		String datfile = "sample_D1a";
		Integer N = 8;

		try {
			inSpecs = d.readSpecsFromFile(PATH+datfile+".in");
		}
		catch (IOException e) {
			System.out.println("in exception: " + e);
		}
		
		execd = d.executeNSpecswCheckRecLarge(inSpecs, N);
		
		try {
			soln = d.readSolnFromFile(PATH+datfile+".out", N);
		}
		catch (IOException e) {
			System.out.println("in exception: " + e);
		}
		
		assertTrue(d.compareExecutedWSoln(execd, soln));
		
	}

	@Test
	public void testD2_8() {
		BuildSpellbook d = new BuildSpellbook();
		Vector<String> inSpecs = null;
		Vector<String> soln = null;
		Vector<String> execd = null;
		String datfile = "sample_D2";
		Integer N = 8;

		try {
			inSpecs = d.readSpecsFromFile(PATH+datfile+".in");
		}
		catch (IOException e) {
			System.out.println("in exception: " + e);
		}
		
		execd = d.executeNSpecswCheckRecSmall(inSpecs, N);
		
		try {
			soln = d.readSolnFromFile(PATH+datfile+".out", N);
		}
		catch (IOException e) {
			System.out.println("in exception: " + e);
		}
		
		assertTrue(d.compareExecutedWSoln(execd, soln));
		
	}

	@Test
	public void testD2a_8() {
		BuildSpellbook d = new BuildSpellbook();
		Vector<String> inSpecs = null;
		Vector<String> soln = null;
		Vector<String> execd = null;
		String datfile = "sample_D2a";
		Integer N = 8;

		try {
			inSpecs = d.readSpecsFromFile(PATH+datfile+".in");
		}
		catch (IOException e) {
			System.out.println("in exception: " + e);
		}
		
		execd = d.executeNSpecswCheckRecSmall(inSpecs, N);
		
		try {
			soln = d.readSolnFromFile(PATH+datfile+".out", N);
		}
		catch (IOException e) {
			System.out.println("in exception: " + e);
		}
		
		assertTrue(d.compareExecutedWSoln(execd, soln));
		
	}


}
