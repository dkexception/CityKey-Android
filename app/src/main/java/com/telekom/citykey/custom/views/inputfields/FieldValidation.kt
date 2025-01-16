/**
 * Copyright (C) 2025 Deutsche Telekom AG
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * SPDX-FileCopyrightText: 2025 Deutsche Telekom AG
 * SPDX-License-Identifier: Apache-2.0
 * License-Filename: LICENSES/Apache-2.0.txt
 */

package com.telekom.citykey.custom.views.inputfields

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class FieldValidation(
    val state: Int,
    val message: String?,
    val stringRes: Int = -1
) : Parcelable {
    companion object {
        const val IDLE = 0
        const val OK = 1
        const val NOT_OK = 2
        const val ERROR = 3
        const val SUCCESS = 4
        const val HELPER = 5
    }
}
