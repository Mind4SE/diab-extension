/**
 * 
 * Copyright Assystem 2011
 * 
 * This file is part of "Mind Compiler" is free software: you can redistribute 
 * it and/or modify it under the terms of the GNU Lesser General Public License 
 * as published by the Free Software Foundation, either version 3 of the 
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT 
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * Author  : Stéphane Seyvoz
 * Contact : sseyvoz@assystem.com 
 * Contributors : julien.tous@orange.com
 */

package org.ow2.mind.compilation.diab;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.objectweb.fractal.adl.ADLException;
import org.ow2.mind.compilation.CompilerCommand;
import org.ow2.mind.compilation.CompilerErrors;
import org.ow2.mind.compilation.ExecutionHelper;
import org.ow2.mind.compilation.ExecutionHelper.ExecutionResult;
import org.ow2.mind.compilation.PreprocessorCommand;
import org.ow2.mind.compilation.gcc.GccCompilerWrapper;

public class DIABCompilerWrapper extends GccCompilerWrapper {

	@Override
	public PreprocessorCommand newPreprocessorCommand(Map<Object, Object> context) {
		return new DIABPreprocessorCommand(context);
	}

	@Override
	public CompilerCommand newCompilerCommand(Map<Object, Object> context) {
		return new DIABCompilerCommand(context);
	}

	// All subclasses are directly inspired from GccCompilerWrapper

	protected class DIABPreprocessorCommand extends GccPreprocessorCommand {

		protected DIABPreprocessorCommand(Map<Object, Object> context) {
			super(context);
		}

		public boolean exec() throws ADLException, InterruptedException {
			final List<String> cmd = new ArrayList<String>();
			cmd.add(this.cmd);
			cmd.add("-E");

			cmd.addAll(flags);

			for (final String def : defines) {
				cmd.add("-D" + def);
			}

			for (final File incDir : includeDir) {
				cmd.add("-I" + incDir.getPath().trim());
			}

			for (final File incFile : includeFile) {
				cmd.add("-include");
				cmd.add(incFile.getPath());
			}

			cmd.add("-@O=" + outputFile.getPath());

			cmd.add(inputFile.getPath());

			// save full command for debug and log purposes
			final StringBuilder sb = new StringBuilder();
			for (final String str : cmd) {
				sb.append(str);
				sb.append(" ");
			}
			fullCmd = sb.toString();

			// execute command
			ExecutionResult result;
			try {
				result = ExecutionHelper.exec(getDescription(), cmd);
			} catch (final IOException e) {
				errorManagerItf.logError(CompilerErrors.EXECUTION_ERROR, this.cmd);
				return false;
			}

			if (result.getExitValue() != 0) {
				errorManagerItf.logError(CompilerErrors.COMPILER_ERROR,
						outputFile.getPath(), result.getOutput());
				return false;
			}
			if (result.getOutput() != null) {
				// command returns 0 and generates an output (warning)
				errorManagerItf.logWarning(CompilerErrors.COMPILER_WARNING,
						outputFile.getPath(), result.getOutput());
			}
			return true;
		}
	}
	protected class DIABCompilerCommand extends GccCompilerCommand {

		protected DIABCompilerCommand(Map<Object, Object> context) {
			super(context);
		}

		public boolean exec() throws ADLException, InterruptedException {

			final List<String> cmd = new ArrayList<String>();
			cmd.add(this.cmd);
			cmd.add("-c");

			cmd.addAll(flags);

			for (final String def : defines) {
				cmd.add("-D" + def);
			}
			for (final File incDir : includeDir) {
				cmd.add("-I" + incDir.getPath().trim());
			}

			for (final File incFile : includeFile) {
				cmd.add("-include");
				cmd.add(incFile.getPath());
			}

			cmd.add("-o");
			cmd.add(outputFile.getPath());

			cmd.add(inputFile.getPath());

			// save full command for debug and log purposes
			final StringBuilder sb = new StringBuilder();
			for (final String str : cmd) {
				sb.append(str);
				sb.append(" ");
			}
			fullCmd = sb.toString();

			// execute command
			ExecutionResult result;
			try {
				result = ExecutionHelper.exec(getDescription(), cmd);
			} catch (final IOException e) {
				errorManagerItf.logError(CompilerErrors.EXECUTION_ERROR, this.cmd);
				return false;
			}

			if (result.getExitValue() != 0) {
				errorManagerItf.logError(CompilerErrors.COMPILER_ERROR,
						outputFile.getPath(), result.getOutput());
				return false;
			}
			if (result.getOutput() != null) {
				// command returns 0 and generates an output (warning)
				errorManagerItf.logWarning(CompilerErrors.COMPILER_WARNING,
						outputFile.getPath(), result.getOutput());
			}
			return true;
		}

		public String getDescription() {
			return "DCC: " + outputFile.getPath();

		}
	}
}

