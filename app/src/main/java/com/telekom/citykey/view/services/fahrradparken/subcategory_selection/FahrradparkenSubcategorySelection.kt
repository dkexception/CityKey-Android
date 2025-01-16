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

package com.telekom.citykey.view.services.fahrradparken.subcategory_selection

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.gms.maps.model.LatLng
import com.telekom.citykey.R
import com.telekom.citykey.databinding.DefectSubcategorySelectionFragmentBinding
import com.telekom.citykey.models.defect_reporter.DefectSubCategory
import com.telekom.citykey.utils.extensions.showDialog
import com.telekom.citykey.utils.extensions.viewBinding
import com.telekom.citykey.view.MainFragment
import com.telekom.citykey.view.services.defect_reporter.category_selection.defectsubcategory.DefectSubcategorySelectionAdapter
import com.telekom.citykey.view.services.fahrradparken.FahrradparkenService
import com.telekom.citykey.view.services.fahrradparken.category_selection.FahrradparkenCategorySelectionDirections
import com.telekom.citykey.view.services.fahrradparken.location_selection.FahrradparkenLocationSelection

class FahrradparkenSubcategorySelection : MainFragment(R.layout.defect_subcategory_selection_fragment) {

    private val binding by viewBinding(DefectSubcategorySelectionFragmentBinding::bind)

    private val args: FahrradparkenSubcategorySelectionArgs by navArgs()
    private var listAdapter: DefectSubcategorySelectionAdapter? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
    }

    private fun initViews() {
        with(binding) {
            setupToolbar(defectSubcategoryToolbar)
            defectSubcategoryToolbar.title = args.selectedCategory.serviceName

            listAdapter = DefectSubcategorySelectionAdapter(categoryResultListener = ::onSubcategorySelection)
            subategoryList.adapter = listAdapter
            listAdapter?.submitList(args.selectedCategory.subCategories)
        }
    }

    private fun onSubcategorySelection(defectSubcategory: DefectSubCategory) {
        if (args.isNewReport) {
            FahrradparkenLocationSelection(
                defectSubcategory.serviceName ?: "",
                defectSubcategory.serviceCode,
                args.service.serviceParams?.get(FahrradparkenService.SERVICE_PARAM_MORE_INFO_BASE_URL),
                locationResultListener = { navigateToFahrradparkenReportCreation(it, defectSubcategory) }
            ).showDialog(childFragmentManager)
        } else {
            findNavController().navigate(
                FahrradparkenCategorySelectionDirections
                    .toExistingReportsFragment(
                        args.service,
                        args.isNewReport,
                        args.selectedCategory,
                        defectSubcategory
                    )
            )
        }
    }

    private fun navigateToFahrradparkenReportCreation(latLng: LatLng, defectSubcategory: DefectSubCategory) {
        findNavController().navigate(
            FahrradparkenSubcategorySelectionDirections.toCreateReportForm(
                args.service,
                args.isNewReport,
                latLng,
                args.selectedCategory,
                defectSubcategory
            )
        )
    }

    override fun onDestroy() {
        super.onDestroy()
        listAdapter = null
    }

}
