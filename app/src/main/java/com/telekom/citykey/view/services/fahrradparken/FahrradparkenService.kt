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

package com.telekom.citykey.view.services.fahrradparken

object FahrradparkenService {
    const val REPORT_STATUS_ICON_ERROR = "Error"
    const val REPORT_STATUS_ICON_DONE = "Done"
    const val REPORT_STATUS_ICON_IN_PROGRESS = "In progress"
    const val REPORT_STATUS_ICON_QUEUED = "Queued"

    const val SERVICE_PARAM_MORE_INFO_BASE_URL = "field_moreInformationBaseURL"

    object Input {
        const val TYPE_OPTIONAL = "OPTIONAL"
        const val TYPE_REQUIRED = "REQUIRED"
        const val TYPE_NOT_REQUIRED = "NOT REQUIRED"

        object Field {
            const val FIRST_NAME = "field_firstName"
            const val LAST_NAME = "field_lastName"
            const val EMAIL = "field_email"
            const val YOUR_CONCERN = "field_yourConcern"
            const val UPLOAD_IMAGE = "field_uploadImage"
            const val CHECK_BOX_TERMS_2 = "field_checkBoxTerms2"
        }
    }
}
