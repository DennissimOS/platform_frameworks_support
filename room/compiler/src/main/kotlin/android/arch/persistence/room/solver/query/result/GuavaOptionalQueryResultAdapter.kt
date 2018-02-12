/*
 * Copyright 2018 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package android.arch.persistence.room.solver.query.result

import android.arch.persistence.room.ext.GuavaBaseTypeNames
import android.arch.persistence.room.ext.L
import android.arch.persistence.room.ext.T
import android.arch.persistence.room.ext.typeName
import android.arch.persistence.room.solver.CodeGenScope

/**
 * Wraps a row adapter when there is only 1 item in the result, and the result's outer type is
 * {@link com.google.common.base.Optional}.
 */
class GuavaOptionalQueryResultAdapter(rowAdapter: RowAdapter)
        : QueryResultAdapter(rowAdapter) {
    val type = rowAdapter.out
    override fun convert(outVarName: String, cursorVarName: String, scope: CodeGenScope) {
        scope.builder().apply {
            rowAdapter?.onCursorReady(cursorVarName, scope)
            val valueVarName = scope.getTmpVar("_value")
            addStatement("final $T $L;", type.typeName(), valueVarName)
            addStatement(
                    "final $T<$T> $L", GuavaBaseTypeNames.OPTIONAL, type.typeName(), outVarName)
            beginControlFlow("if($L.moveToFirst())", cursorVarName).apply {
                // _value = X;
                rowAdapter?.convert(valueVarName, cursorVarName, scope)
                // _outVar = Optional.of(_value);
                addStatement(
                        // _outVar = Optional.of(X);
                        "$L = $T.of($L)",
                        outVarName,
                        GuavaBaseTypeNames.OPTIONAL,
                        valueVarName)
            }
            nextControlFlow("else").apply {
                // _outVar = Optional.absent();
                // Ignore the default value of the type - absent is absent is absent.
                addStatement("$L = $T.absent()", outVarName, GuavaBaseTypeNames.OPTIONAL)
            }
            endControlFlow()
            rowAdapter?.onCursorFinished()?.invoke(scope)
        }
    }
}
