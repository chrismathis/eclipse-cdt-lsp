/*******************************************************************************
 * Copyright (c) 2020 ArSysOp and others.
 *
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Alexander Fedorov (ArSysOp) - initial API and implementation
 *     Gesa Hentschke (Bachmann electronic GmbH) - reused for new LSP Editor
 *******************************************************************************/
package org.eclipse.cdt.lsp.workspace;

import java.util.Optional;
import java.util.function.Function;

import org.eclipse.core.filebuffers.FileBuffers;
import org.eclipse.core.filebuffers.ITextFileBuffer;
import org.eclipse.jface.text.IDocument;

public final class ResolveDocumentBuffer implements Function<IDocument, Optional<ITextFileBuffer>> {

	@Override
	public Optional<ITextFileBuffer> apply(IDocument document) {
		return Optional.ofNullable(document).map(FileBuffers.getTextFileBufferManager()::getTextFileBuffer);
	}

}
