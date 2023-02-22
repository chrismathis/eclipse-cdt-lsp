package org.eclipse.cdt.lsp.editor.ui.test.preference;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;

import org.eclipse.cdt.lsp.LspPlugin;
import org.eclipse.cdt.lsp.editor.ui.test.TestUtils;
import org.eclipse.cdt.lsp.server.ICLanguageServerProvider;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.junit.Before;
import org.junit.Test;

public class LspEditorPreferencesTesterTest {
	private static final String LSP_CEDITOR_ID = "org.eclipse.cdt.lsp.CEditor"; //$NON-NLS-1$
	private static final String C_EDITOR_ID = "org.eclipse.cdt.ui.editor.CEditor"; //$NON-NLS-1$
	private static final String FILE_CONTENT = "// sample file content";
	private static final String MAIN_CPP = "main.cpp";
	private static final String EXTERNAL_HEADER_HPP = "ExternalHeader.hpp";
	private IProject project;
	
	@Before
	public void setUp() throws CoreException {
		project = TestUtils.createCProject("LspCEditorTest"+System.currentTimeMillis());
	}
	
	/**
	 * Tests whether the LS enable returns true on a resource whose project has "Prefer C/C++ Editor (LSP)" enabled.
	 */
	@Test
	public void testLsEnableByUriTest_WITH_LsEditorPreferred() throws CoreException {
		final String fileName = "main.cpp";
		//GIVEN is a project with ENABLED "Prefer C/C++ Editor (LSP)" in the preferences:
		TestUtils.setLspPreferred(project, true);
		//AND a file exits in the given project:
		TestUtils.createFile(project, fileName, FILE_CONTENT);
		//AND a ICLanguageServerProvider which uses LspEditorPreferencesTester as enabledWhen tester:
		ICLanguageServerProvider cLanguageServerProvider = LspPlugin.getDefault().getCLanguageServerProvider();
		//WHEN the LspEditorPreferencesTester gets called by the property tester in the enabledWhen element of the serverProvider extension point,
		//THEN the LspEditorPreferencesTester.test returns TRUE for the given project file URI:
		assertTrue(cLanguageServerProvider.isEnabledFor(project.getFile(fileName).getLocationURI()));		
	}
	
	/**
	 * Tests whether LS enable returns false on a resource whose project has "Prefer C/C++ Editor (LSP)" disabled.
	 */
	@Test
	public void testLsEnableByUriTest_WITHOUT_LsEditorPreferred() throws CoreException {
		final String fileName = "main.cpp";
		//GIVEN is a project with DISABLED "Prefer C/C++ Editor (LSP)" in the preferences:
		TestUtils.setLspPreferred(project, false);
		//AND a file exits in the given project:
		TestUtils.createFile(project, fileName, FILE_CONTENT);
		//AND a ICLanguageServerProvider which uses LspEditorPreferencesTester as enabledWhen tester:
		ICLanguageServerProvider cLanguageServerProvider = LspPlugin.getDefault().getCLanguageServerProvider();
		//WHEN the LspEditorPreferencesTester gets called by the property tester in the enabledWhen element of the serverProvider extension point,
		//THEN the LspEditorPreferencesTester.test returns FALSE for the given project file URI:
		assertTrue(!cLanguageServerProvider.isEnabledFor(project.getFile(fileName).getLocationURI()));		
	}
	
	/**
	 * Tests whether the C/C++ Editor is used for a resource to open whose project has "Prefer C/C++ Editor (LSP)" disabled.
	 */
	@Test
	public void testEditorUsedToOpenFile_WITHOUT_LsEditorPreferred() throws CoreException {
		//GIVEN is a project with DISABLED "Prefer C/C++ Editor (LSP)" in the preferences:
		TestUtils.setLspPreferred(project, false);
		//AND a file exits in the given project:
		var file = TestUtils.createFile(project, MAIN_CPP, FILE_CONTENT);
		//WHEN this file will be opened:
		var editorPart = TestUtils.openInEditor(file);
		//THEN it will be opened in the C/C++ Editor:
		assertEquals(editorPart.getEditorSite().getId(), C_EDITOR_ID);
	}
	
	/**
	 * Tests whether the C/C++ Editor (LSP) is used for a resource to open whose project has "Prefer C/C++ Editor (LSP)" enabled.
	 */
	@Test
	public void testEditorUsedToOpenFile_WITH_LsEditorPreferred() throws CoreException {
		//GIVEN is a project with ENABLED "Prefer C/C++ Editor (LSP)" in the preferences:
		TestUtils.setLspPreferred(project, true);
		//AND a file exits in the given project:
		var file = TestUtils.createFile(project, MAIN_CPP, FILE_CONTENT);
		//WHEN this file will be opened:
		var editorPart = TestUtils.openInEditor(file);
		//THEN it will be opened in the C/C++ Editor (LSP):
		assertEquals(editorPart.getEditorSite().getId(), LSP_CEDITOR_ID);
	}
	
	/**
	 * Tests whether LS enable returns false for an external file which is not opened.
	 */
	@Test
	public void testLsEnableByExternalUriTest_NoEditorOpen() throws CoreException {
		//GIVEN is an external file which does not exists in the given project and is not opened:
		File externalFile = TestUtils.getExternalFile(EXTERNAL_HEADER_HPP);
		//AND a ICLanguageServerProvider which uses LspEditorPreferencesTester as enabledWhen tester:
		ICLanguageServerProvider cLanguageServerProvider = LspPlugin.getDefault().getCLanguageServerProvider();
		//WHEN the LspEditorPreferencesTester gets called by the property tester in the enabledWhen element of the serverProvider extension point,
		//THEN the LspEditorPreferencesTester.test returns FALSE for the given file URI:
		assertTrue(!cLanguageServerProvider.isEnabledFor(externalFile.toURI()));		
	}
	
	/**
	 * Tests whether LS enable returns true for an external file which is opened in the C/C++ Editor (LSP).
	 */	
	@Test
	public void testLsEnableByExternalUriTest_OpenedInLspCEditor() throws CoreException, IOException {
		//GIVEN is an existing external file:
		File externalFile = TestUtils.getExternalFile(EXTERNAL_HEADER_HPP);
		externalFile.createNewFile();
		//AND it's opened in the LSP based C/C++ Editor:
		var editor = TestUtils.openInEditor(externalFile.toURI(), LSP_CEDITOR_ID);
		//AND a ICLanguageServerProvider which uses LspEditorPreferencesTester as enabledWhen tester:
		ICLanguageServerProvider cLanguageServerProvider = LspPlugin.getDefault().getCLanguageServerProvider();
		//WHEN the LspEditorPreferencesTester gets called by the property tester in the enabledWhen element of the serverProvider extension point,
		//THEN the LspEditorPreferencesTester.test returns TRUE for the given file URI:
		assertTrue(cLanguageServerProvider.isEnabledFor(externalFile.toURI()));	
		TestUtils.closeEditor(editor, false);
	}
	
	/**
	 * Tests whether LS enable returns false for an external file which is opened in the C/C++ Editor.
	 */	
	@Test
	public void testLsEnableByExternalUriTest_OpenedInCEditor() throws CoreException, IOException {
		//GIVEN is an existing external file:
		File externalFile = TestUtils.getExternalFile(EXTERNAL_HEADER_HPP);
		externalFile.createNewFile();
		//AND it's opened in the C/C++ Editor:
		var editor = TestUtils.openInEditor(externalFile.toURI(), C_EDITOR_ID);
		//AND a ICLanguageServerProvider which uses LspEditorPreferencesTester as enabledWhen tester:
		ICLanguageServerProvider cLanguageServerProvider = LspPlugin.getDefault().getCLanguageServerProvider();
		//WHEN the LspEditorPreferencesTester gets called by the property tester in the enabledWhen element of the serverProvider extension point,
		//THEN the LspEditorPreferencesTester.test returns FALSE for the given file URI:
		assertTrue(!cLanguageServerProvider.isEnabledFor(externalFile.toURI()));	
		TestUtils.closeEditor(editor, false);
	}

}
