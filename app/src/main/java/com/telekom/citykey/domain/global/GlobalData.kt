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
 * In accordance with Sections 4 and 6 of the License, the following exclusions apply:
 *
 *  1. Trademarks & Logos – The names, logos, and trademarks of the Licensor are not covered by this License and may not be used without separate permission.
 *  2. Design Rights – Visual identities, UI/UX designs, and other graphical elements remain the property of their respective owners and are not licensed under the Apache License 2.0.
 *  3: Non-Coded Copyrights – Documentation, images, videos, and other non-software materials require separate authorization for use, modification, or distribution.
 *
 * These elements are not considered part of the licensed Work or Derivative Works unless explicitly agreed otherwise. All elements must be altered, removed, or replaced before use or distribution. All rights to these materials are reserved, and Contributor accepts no liability for any infringing use. By using this repository, you agree to indemnify and hold harmless Contributor against any claims, costs, or damages arising from your use of the excluded elements.
 *
 * SPDX-FileCopyrightText: 2025 Deutsche Telekom AG
 * SPDX-License-Identifier: Apache-2.0 AND LicenseRef-Deutsche-Telekom-Brand
 * License-Filename: LICENSES/Apache-2.0.txt LICENSES/LicenseRef-Deutsche-Telekom-Brand.txt
 */

package com.telekom.citykey.domain.global

import com.telekom.citykey.R
import com.telekom.citykey.domain.city.CityInteractor
import com.telekom.citykey.domain.global_messager.GlobalMessages
import com.telekom.citykey.data.exceptions.InvalidRefreshTokenException
import com.telekom.citykey.data.exceptions.NoConnectionException
import com.telekom.citykey.domain.user.UserInteractor
import com.telekom.citykey.domain.user.UserState
import com.telekom.citykey.networkinterface.models.content.AvailableCity
import com.telekom.citykey.networkinterface.models.content.City
import com.telekom.citykey.view.user.login.LogoutReason
import io.reactivex.Completable
import io.reactivex.Observable
import timber.log.Timber

class GlobalData(
    private val globalMessages: GlobalMessages,
    private val userInteractor: UserInteractor,
    private val cityInteractor: CityInteractor
) {

    val city: Observable<City> get() = cityInteractor.city
    val user: Observable<UserState> get() = userInteractor.user
    val unexpectedLogout get() = userInteractor.unexpectedLogout
    val currentCityId get() = cityInteractor.currentCityId
    val userId get() = userInteractor.userId
    val userCityName get() = userInteractor.userCityName
    val cityColor get() = cityInteractor.cityColor
    val cityName get() = cityInteractor.cityName
    val cityLocation get() = cityInteractor.cityLocation
    val isUserBrowsingHomeCity get() = userInteractor.isUserLoggedIn && userInteractor.userCityId == cityInteractor.currentCityId
    val isUserLoggedIn get() = userInteractor.isUserLoggedIn
    val hasUserAcceptedDpn get() = userInteractor.hasAcceptedDpn

    private var _shouldRefreshServices = false
    val shouldRefreshServices: Boolean
        get() = _shouldRefreshServices

    fun refreshContent(): Completable = cityInteractor.loadCity()
        .flatMapCompletable {
            if (userInteractor.isUserLoggedIn)
                userInteractor.updateUser().ignoreElement()
            else Completable.complete()
        }
        .doOnError {
            when (it) {
                is NoConnectionException ->
                    globalMessages.displayToast(R.string.dialog_no_internet)

                is InvalidRefreshTokenException ->
                    logOutUser(it.reason)

                else -> Timber.e(it)
            }
        }
        .onErrorComplete()

    fun loadCity(availableCity: AvailableCity) = cityInteractor.loadCity(availableCity)

    fun loadUser(isLoggedIn: Boolean = true): Completable =
        if (isLoggedIn) userInteractor.updateUser()
            .ignoreElement()
        else {
            userInteractor.setUserAbsent()
            Completable.complete()
        }

    fun logOutUser(logoutReason: LogoutReason = LogoutReason.TECHNICAL_LOGOUT) {
        userInteractor.logOutUser(logoutReason)
    }

    fun setServicesToReload() {
        _shouldRefreshServices = true
    }

    fun markGetServicesCompleted() {
        _shouldRefreshServices = false
    }
}
