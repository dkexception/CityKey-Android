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

package com.telekom.citykey.view.user.profile

import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.view.MenuItem
import androidx.activity.enableEdgeToEdge
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.vectordrawable.graphics.drawable.AnimatedVectorDrawableCompat
import com.telekom.citykey.R
import com.telekom.citykey.databinding.ProfileActivityBinding
import com.telekom.citykey.utils.extensions.applySafeAllInsetsWithSides
import com.telekom.citykey.utils.extensions.disableRecentsScreenshot
import com.telekom.citykey.utils.extensions.preventContentSharing
import com.telekom.citykey.utils.extensions.setAccessibilityRoleForToolbarTitle
import com.telekom.citykey.utils.extensions.viewBinding
import com.telekom.citykey.view.user.login.LoginActivity

class ProfileActivity : AppCompatActivity() {

    private val binding by viewBinding(ProfileActivityBinding::inflate)

    private val toX: AnimatedVectorDrawableCompat by lazy {
        AnimatedVectorDrawableCompat.create(applicationContext, R.drawable.back_to_x)!!
    }
    private val toBack: AnimatedVectorDrawableCompat by lazy {
        AnimatedVectorDrawableCompat.create(applicationContext, R.drawable.x_to_back)!!
    }
    private var isIndicatorCross = true

    var backAction = ProfileBackActions.LOGOUT

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        disableRecentsScreenshot()
        preventContentSharing()
        enableEdgeToEdge()

        setContentView(binding.root)
        setupUI()
        handleWindowInsets()
    }

    private fun setupUI() {
        initToolbar()
    }

    private fun initToolbar() {
        binding.toolbarProfile.setNavigationIcon(R.drawable.ic_profile_close)
        binding.toolbarProfile.setNavigationIconTint(getColor(R.color.onSurface))
        binding.toolbarProfile.setNavigationContentDescription(R.string.accessibility_btn_close)
        binding.toolbarProfile.setNavigationOnClickListener { onBackPressed() }
    }

    private fun handleWindowInsets() {
        binding.containerAppBarLayout.applySafeAllInsetsWithSides(top = true, left = true, right = true)
        binding.profileNavHost.applySafeAllInsetsWithSides(left = true, right = true, bottom = true)
    }

    fun adaptToolbarForClose() {
        if (!isIndicatorCross) {
            binding.toolbarProfile.navigationIcon = toX.apply { start() }
            isIndicatorCross = true
        }
    }

    fun adaptToolbarForBack() {
        if (isIndicatorCross) {
            binding.toolbarProfile.navigationIcon = toBack.apply { start() }
            isIndicatorCross = false
        }
    }

    fun logOut() {
        startActivity(
            Intent(applicationContext, LoginActivity::class.java).apply {
                putExtra(LoginActivity.LAUNCH_PROFILE, true)
            }
        )
        finish()
    }

    fun setPageTitle(@StringRes resId: Int) {
        binding.toolbarProfile.setTitle(resId)
        setAccessibilityRoleForToolbarTitle(binding.toolbarProfile)
    }

    override fun onBackPressed() {
        when (backAction) {
            ProfileBackActions.LOGOUT ->
                logOut()

            ProfileBackActions.BACK ->
                super.onBackPressed()

            ProfileBackActions.POP_TO_PROFILE ->
                findNavController(R.id.profile_nav_host).popBackStack(R.id.profile, false)

            ProfileBackActions.FINISH ->
                finish()
        }
    }

    override fun onDestroy() {
        backAction = ProfileBackActions.BACK
        super.onDestroy()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        return when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        finish()
    }
}
