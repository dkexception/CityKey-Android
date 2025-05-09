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

package com.telekom.citykey.view.services.poi.categories

import android.content.DialogInterface
import android.content.res.ColorStateList
import android.os.Bundle
import android.view.View
import androidx.core.widget.TextViewCompat
import com.telekom.citykey.R
import com.telekom.citykey.databinding.PoiCategoriesFragmentBinding
import com.telekom.citykey.domain.city.CityInteractor
import com.telekom.citykey.networkinterface.models.poi.PoiCategory
import com.telekom.citykey.utils.DialogUtil
import com.telekom.citykey.utils.KoverIgnore
import com.telekom.citykey.utils.extensions.applySafeAllInsetsWithSides
import com.telekom.citykey.utils.extensions.getColor
import com.telekom.citykey.utils.extensions.setAccessibilityRoleForToolbarTitle
import com.telekom.citykey.utils.extensions.setVisible
import com.telekom.citykey.utils.extensions.viewBinding
import com.telekom.citykey.view.FullScreenBottomSheetDialogFragment
import org.koin.androidx.viewmodel.ext.android.viewModel

@KoverIgnore
class PoiCategorySelection(
    private val category: PoiCategory? = null,
    private var onDismiss: ((Boolean) -> Unit)? = null
) : FullScreenBottomSheetDialogFragment(R.layout.poi_categories_fragment) {

    private val binding by viewBinding(PoiCategoriesFragmentBinding::bind)

    private val viewModel: PoiCategorySelectionViewModel by viewModel()
    private var listAdapter: PoiCategorySelectionAdapter? = null

    private var isCategorySelected: Boolean = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.toolbarPoiCategory.navigationContentDescription =
            getString(R.string.accessibility_btn_close)
        binding.toolbarPoiCategory.setNavigationIcon(R.drawable.ic_profile_close)
        binding.toolbarPoiCategory.setNavigationIconTint(getColor(R.color.onSurface))
        binding.toolbarPoiCategory.setNavigationOnClickListener {
            dismiss()
        }
        setAccessibilityRoleForToolbarTitle(binding.toolbarPoiCategory)

        binding.retryButton.setTextColor(CityInteractor.cityColorInt)
        TextViewCompat.setCompoundDrawableTintList(
            binding.retryButton,
            ColorStateList.valueOf(CityInteractor.cityColorInt)
        )
        binding.retryButton.setOnClickListener {
            binding.errorLayout.setVisible(false)
            binding.loading.setVisible(true)
            viewModel.onRetry()
        }

        listAdapter = PoiCategorySelectionAdapter(viewModel::onCategorySelected, category)
        binding.categoryServicesList.adapter = listAdapter

        handleWindowInsets()

        subscribeUi()
    }

    private fun handleWindowInsets() {
        binding.poiCategoryAppBar.applySafeAllInsetsWithSides(left = true, right = true)
        binding.categoryServicesList.applySafeAllInsetsWithSides(left = true, right = true, bottom = true)
        binding.errorLayout.applySafeAllInsetsWithSides(left = true, right = true, bottom = true)
    }

    private fun subscribeUi() {
        viewModel.categoryListItems.observe(viewLifecycleOwner) { categories ->
            binding.loading.setVisible(false)
            binding.errorLayout.setVisible(categories.isEmpty())
            binding.categoryServicesList.setVisible(categories.isNotEmpty())
            if (categories.isNotEmpty()) listAdapter?.submitList(categories)
        }
        viewModel.poiDataAvailable.observe(viewLifecycleOwner) {
            isCategorySelected = true
            dismiss()
        }
        viewModel.showRetryDialog.observe(viewLifecycleOwner) {
            DialogUtil.showRetryDialog(requireContext(), viewModel::onRetryRequired) {
                viewModel.onRetryCanceled()
                listAdapter?.stopLoading()
            }
        }
        viewModel.technicalError.observe(viewLifecycleOwner) {
            listAdapter?.stopLoading()
            DialogUtil.showTechnicalError(requireContext())
        }
    }

    override fun onDismiss(dialog: DialogInterface) {
        onDismiss?.invoke(isCategorySelected)
        super.onDismiss(dialog)
        listAdapter = null
    }
}
