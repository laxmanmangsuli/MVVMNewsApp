package com.androiddevs.mvvmnewsapp.ui

import android.os.Bundle
import android.util.SparseArray
import androidx.core.util.forEach
import androidx.core.view.forEach
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.androiddevs.mvvmnewsapp.R
import com.google.android.material.bottomnavigation.BottomNavigationView

class BottomNavManager(
    private var fragmentManager : FragmentManager,
    private val containerId :Int,
    private val bottomNavigationView: BottomNavigationView

) {

    companion object{

        private const val KEY_NAV_HISTORY = "nav_history"
    }

    private val navGraphIds =
        listOf(R.navigation.nav_news, R.navigation.nav_saved, R.navigation.nav_search)

    private val graphIdToTagMap = SparseArray<String>()

    private var navGraphStartDestinations = mutableMapOf<Int, Int>()

    private var navHistory = BottomNavHistory().apply { push(R.id.nav_news) }

    private var selectedNavController: NavController? = null

    fun onBottomNavChanged(listener: NavController.OnDestinationChangedListener) {
        selectedNavController?.addOnDestinationChangedListener(listener)
    }

    init {
        setupNavController()
    }

    fun setupNavController() {
        navGraphStartDestinations.clear()
        graphIdToTagMap.clear()

        // create a NavHostFragment for each NavGraph ID
        createNavHostFragmentsForGraphs()

        // When a navigation item is selected
        bottomNavigationView.setupItemClickListener()
    }

    private fun createNavHostFragmentsForGraphs() {
        // create a NavHostFragment for each NavGraph ID
        navGraphIds.forEachIndexed { index, navGraphId ->
            val fragmentTag = getFragmentTag(index)

            // Find or create the Navigation host fragment
            val navHostFragment = obtainNavHostFragment(
                fragmentTag,
                navGraphId
            )

            // Obtain its id
            val graphId = navHostFragment.navController.graph.id
            navGraphStartDestinations[graphId] = navHostFragment.navController.graph.startDestination

            // Save to the map
            graphIdToTagMap[graphId] = fragmentTag

            // Attach or detach nav host fragment depending on whether it's the selected item.
            if (bottomNavigationView.selectedItemId == graphId) {
                // Update nav controller with the selected graph
                selectedNavController = navHostFragment.navController
                showNavHostFragment(navHostFragment, true)
            } else {
                showNavHostFragment(navHostFragment, false)
            }
        }
    }

    private fun BottomNavigationView.setupItemClickListener() {
        menu.forEach { item ->
            item.setOnMenuItemClickListener {

                // do nothing on tab re-selection
                if (item.isChecked) {
                    return@setOnMenuItemClickListener true
                }

                if (!fragmentManager.isStateSaved) {
                    item.isChecked = true
                    navHistory.push(item.itemId)

                    val newlySelectedItemTag = graphIdToTagMap[item.itemId]
                    val selectedFragment = fragmentManager.findFragmentByTag(newlySelectedItemTag)
                            as NavHostFragment

                    fragmentManager.beginTransaction()
                        .show(selectedFragment)
                        .setMaxLifecycle(selectedFragment, Lifecycle.State.RESUMED)
                        .setPrimaryNavigationFragment(selectedFragment)
                        .apply {
                            // Detach all other Fragments
                            graphIdToTagMap.forEach { _, fragmentTag ->
                                if (fragmentTag != newlySelectedItemTag) {
                                    val fragment = fragmentManager.findFragmentByTag(fragmentTag)!!
                                    hide(fragment)
                                    setMaxLifecycle(fragment, Lifecycle.State.STARTED)
                                }
                            }
                        }
                        .commit()

                    selectedNavController = selectedFragment.navController
                }

                true
            }
        }
    }

    private fun selectItem(itemId: Int) {
        bottomNavigationView.menu.findItem(itemId)
            ?.let {
                bottomNavigationView.menu.performIdentifierAction(itemId, 0)
            }
    }
    fun onBackPressed(): Boolean {
        return if (navHistory.isNotEmpty) {
            selectedNavController?.let {
                if (it.currentDestination == null || it.currentDestination?.id == navGraphStartDestinations[bottomNavigationView.selectedItemId]) {
                    if (isFirstTab()) return false

                    navHistory.pop(bottomNavigationView.selectedItemId)
                    selectItem(navHistory.current())
                    return true
                }
                return false // super.onBackPressed() will be called, which will pop the fragment itself
            } ?: false
        } else false
    }

    private fun isFirstTab(): Boolean {
        return bottomNavigationView.selectedItemId == R.id.nav_news
    }


    private fun obtainNavHostFragment(
        fragmentTag: String,
        navGraphId: Int
    ): NavHostFragment {
        // If the Nav Host fragment exists, return it
        val existingFragment = fragmentManager.findFragmentByTag(fragmentTag) as NavHostFragment?
        existingFragment?.let { return it }

        // Otherwise, create it and return it.
        val navHostFragment = NavHostFragment.create(navGraphId)
        fragmentManager.beginTransaction()
            .add(containerId, navHostFragment, fragmentTag)
            .commitNow()
        return navHostFragment
    }

    fun onSaveInstanceState(outState: Bundle?) {
        outState?.putParcelable(KEY_NAV_HISTORY, navHistory)
    }
    private fun FragmentManager.isOnBackStack(backStackName: String): Boolean {
        val backStackCount = backStackEntryCount
        for (index in 0 until backStackCount) {
            if (getBackStackEntryAt(index).name == backStackName) {
                return true
            }
        }
        return false
    }

    fun onRestoreInstanceState(savedInstanceState: Bundle?) {
        savedInstanceState?.let {
            navHistory = it.getParcelable<BottomNavHistory>(KEY_NAV_HISTORY) as BottomNavHistory
        }
    }

    private fun showNavHostFragment(
        navHostFragment: NavHostFragment,
        show: Boolean
    ) {
        fragmentManager.beginTransaction()
            .apply {
                if (show) {
                    show(navHostFragment)
                    setMaxLifecycle(navHostFragment, Lifecycle.State.RESUMED)
                } else {
                    hide(navHostFragment)
                    setMaxLifecycle(navHostFragment, Lifecycle.State.STARTED)
                }
            }
            .commitNow()
    }

    private fun getFragmentTag(index: Int) = "BottomNavManager#$index"
}

