@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)

package com.nomanhassan.medremind.presentation.medication_list

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PlainTooltip
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Surface
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.TooltipAnchorPosition
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.material3.rememberTooltipState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.backhandler.BackHandler
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.nomanhassan.medremind.app.ui.theme.MedRemindTheme
import com.nomanhassan.medremind.core.enums.TimeSlot
import com.nomanhassan.medremind.core.presentation.ObserveAsEvents
import com.nomanhassan.medremind.core.presentation.UiText
import com.nomanhassan.medremind.core.presentation.components.ConfirmationDialog
import com.nomanhassan.medremind.core.presentation.components.ConfirmationDialogConfig
import com.nomanhassan.medremind.core.presentation.components.FloatingToolbarDefaults
import com.nomanhassan.medremind.core.presentation.components.HorizontalFloatingToolbar
import com.nomanhassan.medremind.core.util.PlatformType
import com.nomanhassan.medremind.core.util.getPlatformType
import com.nomanhassan.medremind.core.util.toLocalizedDigits
import com.nomanhassan.medremind.domain.model.Medication
import com.nomanhassan.medremind.presentation.medication_list.components.DateDisplayCard
import com.nomanhassan.medremind.presentation.medication_list.components.EmptyState
import com.nomanhassan.medremind.presentation.medication_list.components.FilterBottomSheet
import com.nomanhassan.medremind.presentation.medication_list.components.MedicationListItem
import com.nomanhassan.medremind.presentation.medication_list.components.MedicationSearchBar
import kotlinx.coroutines.delay
import medremind.composeapp.generated.resources.Res
import medremind.composeapp.generated.resources.action_undo
import medremind.composeapp.generated.resources.app_name
import medremind.composeapp.generated.resources.btn_add_medication_description
import medremind.composeapp.generated.resources.btn_cancel
import medremind.composeapp.generated.resources.btn_delete
import medremind.composeapp.generated.resources.btn_delete_medication_description
import medremind.composeapp.generated.resources.btn_deselect_all_description
import medremind.composeapp.generated.resources.btn_filter_description
import medremind.composeapp.generated.resources.btn_select_all_description
import medremind.composeapp.generated.resources.btn_settings_description
import medremind.composeapp.generated.resources.delete_medication
import medremind.composeapp.generated.resources.delete_medication_dialog_message
import medremind.composeapp.generated.resources.empty_filter_subtitle
import medremind.composeapp.generated.resources.empty_filter_title
import medremind.composeapp.generated.resources.empty_list_subtitle
import medremind.composeapp.generated.resources.empty_list_title
import medremind.composeapp.generated.resources.icon_app_description
import medremind.composeapp.generated.resources.icon_close
import medremind.composeapp.generated.resources.icon_deselect_all
import medremind.composeapp.generated.resources.icon_filled_add
import medremind.composeapp.generated.resources.icon_outline_rounded_delete
import medremind.composeapp.generated.resources.icon_outline_search
import medremind.composeapp.generated.resources.icon_outline_settings
import medremind.composeapp.generated.resources.icon_rounded_filter_list
import medremind.composeapp.generated.resources.icon_search_description
import medremind.composeapp.generated.resources.icon_select_all
import medremind.composeapp.generated.resources.logo
import medremind.composeapp.generated.resources.item_selected
import medremind.composeapp.generated.resources.your_medications_title
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.pluralStringResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel
import kotlin.math.abs

@Composable
fun MedicationListScreenRoot(
    viewModel: MedicationListViewModel = koinViewModel(),
    onClickMedicationItem: (Int) -> Unit,
    onClickSettings: () -> Unit,
    onClickAddMedication: () -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    val snackbarState = remember { SnackbarHostState() }
    
    val undoLabel = stringResource(Res.string.action_undo)
    
    ObserveAsEvents(viewModel.events) { event ->
        when (event) {
            is MedicationListEvent.OnMedicationDeleteSuccess -> {
                snackbarState.showSnackbar(
                    message = event.message,
                    withDismissAction = true
                )
            }
            is MedicationListEvent.OnMedicationDeleteError -> {
                snackbarState.showSnackbar(
                    message = event.message,
                    withDismissAction = true
                )
            }
            is MedicationListEvent.OnShowUndoSnackbar -> {
                val result = snackbarState.showSnackbar(
                    message = event.message,
                    actionLabel = undoLabel,
                    duration = SnackbarDuration.Short
                )
                if (result == SnackbarResult.ActionPerformed) {
                    viewModel.onAction(MedicationListAction.OnUndoDelete)
                }
            }
        }
    }

    MedicationListScreen(
        state = state,
        snackbarState = snackbarState,
        onAction = { action ->
            when (action) {
                is MedicationListAction.OnClickMedicationItem -> onClickMedicationItem(action.id)
                MedicationListAction.OnClickSettings -> onClickSettings()
                MedicationListAction.OnClickAddMedication -> onClickAddMedication()
                else -> Unit
            }
            viewModel.onAction(action)
        }
    )
}

@Composable
fun MedicationListScreen(
    state: MedicationListState,
    snackbarState: SnackbarHostState,
    onAction: (MedicationListAction) -> Unit,
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current
    val focusRequester = remember { FocusRequester() }

    BackHandler(state.isSelectionMode || state.isSearchBarVisible) {
        focusManager.clearFocus()
        if (state.isSearchBarVisible) {
            onAction(MedicationListAction.OnToggleSearch)
        } else onAction(MedicationListAction.OnCancelSelection)
    }
    
    val lazyListState = rememberLazyListState()
    
    var isScrollInitialized by remember { mutableStateOf(false) }
    var isFocusInitialized by remember { mutableStateOf(false) }
    
    val isScrolled by remember { derivedStateOf {
        lazyListState.firstVisibleItemIndex > 0 || lazyListState.firstVisibleItemScrollOffset > 100
    } }
    
    LaunchedEffect(state.searchQuery, state.selectedTimeSlot) {
        if (!isScrollInitialized) {
            isScrollInitialized = true
            return@LaunchedEffect
        }
        
        if (state.medicationItems.isNotEmpty()) {
            lazyListState.animateScrollToItem(0)
        }
    }
    
    LaunchedEffect(state.isSearchBarVisible) {
        if (!isFocusInitialized) {
            isFocusInitialized = true
            return@LaunchedEffect
        }
        
        if (state.isSearchBarVisible) {
            delay(150)
            focusRequester.requestFocus()
        } else {
            focusManager.clearFocus()
            keyboardController?.hide()
        }
    }

    val vibrantColors = FloatingToolbarDefaults.standardFloatingToolbarColors(
        toolbarContainerColor = MaterialTheme.colorScheme.primary,
        toolbarContentColor = MaterialTheme.colorScheme.onPrimary,
    )
    
    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectTapGestures(
                    onTap = { focusManager.clearFocus() }
                )
            },
        topBar = {
            Column(
                modifier = Modifier
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.1f),
                                MaterialTheme.colorScheme.primary
                            )
                        )
                    ),
                horizontalAlignment = Alignment.End
            ) {
                TopAppBar(
                    title = {
                        if (state.isSelectionMode) {
                            Text(stringResource(Res.string.item_selected, state.selectedMedicationIds.size).toLocalizedDigits())
                        } else {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    painter = painterResource(Res.drawable.logo),
                                    contentDescription = stringResource(Res.string.icon_app_description),
                                    modifier = Modifier.size(32.dp)
                                )
                                Text(stringResource(Res.string.app_name))
                            }
                        }
                    },
                    actions = {
                        TooltipBox(
                            positionProvider = TooltipDefaults.rememberTooltipPositionProvider(TooltipAnchorPosition.Above),
                            tooltip = {
                                PlainTooltip {
                                    Text(
                                        text = stringResource(Res.string.icon_search_description),
                                        style = MaterialTheme.typography.titleMedium
                                    )
                                }
                            },
                            state = rememberTooltipState(),
                        ) {
                            IconButton(
                                onClick = { onAction(MedicationListAction.OnToggleSearch) }
                            ) {
                                Icon(
                                    painter = painterResource(Res.drawable.icon_outline_search),
                                    contentDescription = stringResource(Res.string.icon_search_description),
                                    tint = MaterialTheme.colorScheme.onPrimary
                                )
                            }
                        }
                        
                        if (!state.isSelectionMode) {
                            if (getPlatformType() == PlatformType.ANDROID) {
                                TooltipBox(
                                    positionProvider = TooltipDefaults.rememberTooltipPositionProvider(TooltipAnchorPosition.Above),
                                    tooltip = {
                                        PlainTooltip {
                                            Text(
                                                text = stringResource(Res.string.btn_add_medication_description),
                                                style = MaterialTheme.typography.titleMedium
                                            )
                                        }
                                    },
                                    state = rememberTooltipState(),
                                ) {
                                    IconButton(
                                        onClick = { onAction(MedicationListAction.OnClickAddMedication) }
                                    ) {
                                        Icon(
                                            painter = painterResource(Res.drawable.icon_filled_add),
                                            contentDescription = stringResource(Res.string.btn_add_medication_description),
                                            tint = MaterialTheme.colorScheme.onPrimary,
                                            modifier = Modifier.size(28.dp)
                                        )
                                    }
                                }
                            }

                            TooltipBox(
                                positionProvider = TooltipDefaults.rememberTooltipPositionProvider(TooltipAnchorPosition.Above),
                                tooltip = {
                                    PlainTooltip {
                                        Text(
                                            text = stringResource(Res.string.btn_settings_description),
                                            style = MaterialTheme.typography.titleMedium
                                        )
                                    }
                                },
                                state = rememberTooltipState(),
                            ) {
                                IconButton(
                                    onClick = { onAction(MedicationListAction.OnClickSettings) }
                                ) {
                                    Icon(
                                        painter = painterResource(Res.drawable.icon_outline_settings),
                                        contentDescription = stringResource(Res.string.btn_settings_description),
                                        tint = MaterialTheme.colorScheme.onPrimary
                                    )
                                }
                            }
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.Transparent,
                        titleContentColor = MaterialTheme.colorScheme.onPrimary
                    )
                )
                
                // Search Bar
                AnimatedVisibility(
                    visible = state.isSearchBarVisible
                ) {
                    MedicationSearchBar(
                        searchQuery = state.searchQuery,
                        onSearchQueryChange = {
                            onAction(MedicationListAction.OnSearchQueryChange(it))
                        },
                        onClearSearch = {
                            onAction(MedicationListAction.OnClearSearch)
                        },
                        onImeSearch = {
                            keyboardController?.hide()
                            focusManager.clearFocus()
                        },
                        modifier = Modifier
                            .focusRequester(focusRequester)
                            .widthIn(max = 500.dp)
                            .fillMaxWidth()
                            .padding(start = 12.dp, end = 12.dp, bottom = 6.dp)
                    )
                }
            }
        },
        snackbarHost = {
            SnackbarHost(snackbarState)
        },
        floatingActionButton = {
            if (getPlatformType() == PlatformType.ANDROID && !state.isSelectionMode) {
                TooltipBox(
                    positionProvider = TooltipDefaults.rememberTooltipPositionProvider(TooltipAnchorPosition.Above),
                    tooltip = {
                        PlainTooltip {
                            Text(
                                text = stringResource(Res.string.btn_add_medication_description),
                                style = MaterialTheme.typography.titleMedium
                            )
                        }
                    },
                    state = rememberTooltipState(),
                ) {
                    FloatingActionButton(
                        onClick = {
                            onAction(MedicationListAction.OnClickAddMedication)
                        },
                        elevation = FloatingActionButtonDefaults.elevation(
                            defaultElevation = 3.dp,
                            pressedElevation = 3.dp,
                            focusedElevation = 3.dp,
                            hoveredElevation = 6.dp
                        ),
                        modifier = Modifier.padding(bottom = 12.dp)
                    ) {
                        Icon(
                            painter = painterResource(Res.drawable.icon_filled_add),
                            contentDescription = stringResource(Res.string.btn_add_medication_description)
                        )
                    }
                }
            }
            
            if (state.isSelectionMode) {
                HorizontalFloatingToolbar(
                    expanded = true,
                    expandedShadowElevation = 2.dp,
                    colors = vibrantColors,
                ) {
                    val visibleIds = state.medicationItems.map { it.medication.medicationId }.toSet()
                    val isAllSelected = visibleIds.isNotEmpty() &&
                            state.selectedMedicationIds.containsAll(visibleIds)

                    TooltipBox(
                        positionProvider = TooltipDefaults.rememberTooltipPositionProvider(TooltipAnchorPosition.Above),
                        tooltip = {
                            PlainTooltip {
                                Text(
                                    text = stringResource(Res.string.btn_cancel),
                                    style = MaterialTheme.typography.titleMedium
                                )
                            }
                        },
                        state = rememberTooltipState(),
                    ) {
                        IconButton(
                            onClick = { onAction(MedicationListAction.OnCancelSelection) }
                        ) {
                            Icon(
                                painter = painterResource(Res.drawable.icon_close),
                                contentDescription = stringResource(Res.string.btn_cancel),
                                tint = MaterialTheme.colorScheme.onPrimary
                            )
                        }
                    }

                    TooltipBox(
                        positionProvider = TooltipDefaults.rememberTooltipPositionProvider(TooltipAnchorPosition.Above),
                        tooltip = {
                            PlainTooltip {
                                Text(
                                    text = stringResource(if (isAllSelected) Res.string.btn_deselect_all_description else Res.string.btn_select_all_description),
                                    style = MaterialTheme.typography.titleMedium
                                )
                            }
                        },
                        state = rememberTooltipState(),
                    ) {
                        IconButton(
                            onClick = { onAction(MedicationListAction.OnToggleSelectAll) }
                        ) {
                            Icon(
                                painter = painterResource(
                                    if (isAllSelected) Res.drawable.icon_deselect_all
                                    else Res.drawable.icon_select_all
                                ),
                                contentDescription = stringResource(
                                    if (isAllSelected) Res.string.btn_deselect_all_description
                                    else Res.string.btn_select_all_description
                                ),
                                tint = MaterialTheme.colorScheme.onPrimary
                            )
                        }
                    }

                    TooltipBox(
                        positionProvider = TooltipDefaults.rememberTooltipPositionProvider(TooltipAnchorPosition.Above),
                        tooltip = {
                            PlainTooltip {
                                Text(
                                    text = stringResource(Res.string.btn_delete_medication_description),
                                    style = MaterialTheme.typography.titleMedium
                                )
                            }
                        },
                        state = rememberTooltipState(),
                    ) {
                        IconButton(
                            onClick = {
                                onAction(MedicationListAction.OnClickDelete)
                            },
                            enabled = state.selectedMedicationIds.isNotEmpty()
                        ) {
                            Icon(
                                painter = painterResource(Res.drawable.icon_outline_rounded_delete),
                                contentDescription = stringResource(Res.string.btn_delete_medication_description),
                                tint = MaterialTheme.colorScheme.onError
                            )
                        }
                    }
                }
            }
        },
        floatingActionButtonPosition = if (state.isSelectionMode) FabPosition.Center else FabPosition.End,
        containerColor = MaterialTheme.colorScheme.primary
    ) { paddingValues ->
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            color = MaterialTheme.colorScheme.surfaceVariant,
            shape = RoundedCornerShape(
                topStart = 16.dp,
                topEnd = 16.dp
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                DateDisplayCard(
                    dayOfWeek = state.dayOfWeek?.asString() ?: "",
                    dayOfMonth = state.dayOfMonth,
                    month = state.month?.asString() ?: "",
                    year = state.year,
                    isCollapsed = isScrolled,
                    modifier = Modifier
                        .widthIn(max = 500.dp)
                        .fillMaxWidth()
                        .padding(vertical = 12.dp)
                        .animateContentSize()
                )

                Row(
                    modifier = Modifier
                        .widthIn(max = 500.dp)
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = stringResource(Res.string.your_medications_title, state.medicationItems.size.toLocalizedDigits()),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )

                    TooltipBox(
                        positionProvider = TooltipDefaults.rememberTooltipPositionProvider(TooltipAnchorPosition.Above),
                        tooltip = {
                            PlainTooltip {
                                Text(
                                    text = stringResource(Res.string.btn_filter_description),
                                    style = MaterialTheme.typography.titleMedium
                                )
                            }
                        },
                        state = rememberTooltipState(),
                    ) {
                        IconButton(
                            onClick = { onAction(MedicationListAction.OnToggleFilterMenu) }
                        ) {
                            val isFilterActive = state.selectedTimeSlot != TimeSlot.ALL
                            val iconColor = if (isFilterActive) {
                                MaterialTheme.colorScheme.primary
                            } else MaterialTheme.colorScheme.onSurfaceVariant

                            Icon(
                                painter = painterResource(Res.drawable.icon_rounded_filter_list),
                                contentDescription = stringResource(Res.string.btn_filter_description),
                                tint = iconColor
                            )
                        }
                    }
                }
                Spacer(Modifier.height(4.dp))

                // Medications List or Empty State
                if (state.isLoading) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Spacer(modifier = Modifier.weight(1f))
                        CircularProgressIndicator()
                        Spacer(modifier = Modifier.weight(3f))
                    }
                } else if (state.medicationItems.isEmpty()) {
                    val isFiltering = state.searchQuery.isNotEmpty() || state.selectedTimeSlot != TimeSlot.ALL
                    
                    if (isFiltering) {
                        EmptyState(
                            title = stringResource(Res.string.empty_filter_title),
                            subtitle = stringResource(Res.string.empty_filter_subtitle)
                        )
                    } else {
                        EmptyState(
                            title = stringResource(Res.string.empty_list_title),
                            subtitle = stringResource(Res.string.empty_list_subtitle)
                        )
                    }
                } else {
                    LazyColumn(
                        state = lazyListState,
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(4.dp),
                        contentPadding = PaddingValues(bottom = 100.dp)
                    ) {
                        items(
                            items = state.medicationItems,
                            key = { it.medication.medicationId }
                        ) { medicationItem ->
                            val dismissState = rememberSwipeToDismissBoxState(
                                positionalThreshold = { distance -> distance * 0.4f }
                            )
                            
//                            LaunchedEffect(dismissState) {
//                                snapshotFlow { dismissState.currentValue }
//                                    .collect { currentValue ->
//                                        if (currentValue != SwipeToDismissBoxValue.Settled) {
//                                            onAction(MedicationListAction.OnDeleteIndividual(medicationItem.medication))
//                                            dismissState.snapTo(SwipeToDismissBoxValue.Settled)
//                                        }
//                                    }
//                            }

                            when (dismissState.currentValue) {
                                SwipeToDismissBoxValue.EndToStart,
                                SwipeToDismissBoxValue.StartToEnd -> {
                                    LaunchedEffect(dismissState.currentValue) {
                                        onAction(MedicationListAction.OnDeleteIndividual(medicationItem.medication))
                                        // Reset the state so the item doesn't stay "swiped" if the list recomposes
                                        dismissState.snapTo(SwipeToDismissBoxValue.Settled)
                                    }
                                }
                                SwipeToDismissBoxValue.Settled -> { /* Do nothing */ }
                            }

                            val isSwipedFarEnough = dismissState.targetValue != SwipeToDismissBoxValue.Settled
                            val haptic = LocalHapticFeedback.current
                            LaunchedEffect(isSwipedFarEnough) {
                                if (isSwipedFarEnough) {
                                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                }
                            }

                            SwipeToDismissBox(
                                state = dismissState,
                                gesturesEnabled = !state.isSelectionMode,
                                backgroundContent = {
                                    val offset = try { dismissState.requireOffset() } catch (e: Exception) { 0f }
                                    val dragAmountDp = with(LocalDensity.current) { abs(offset).toDp() }

                                    // 2. Spring Animation for the Width
                                    // This makes the background "pop" open or bounce when it changes size
                                    val animatedWidth by animateDpAsState(
                                        targetValue = dragAmountDp,
                                        animationSpec = spring(
                                            dampingRatio = Spring.DampingRatioMediumBouncy,
                                            stiffness = Spring.StiffnessLow
                                        ),
                                        label = "widthSpring"
                                    )

                                    // 3. Spring Animation for the Icon Scale
                                    val scale by animateFloatAsState(
                                        targetValue = if (isSwipedFarEnough) 1.4f else 1.0f,
                                        animationSpec = spring(
                                            dampingRatio = if (isSwipedFarEnough) Spring.DampingRatioHighBouncy else Spring.DampingRatioNoBouncy,
                                            stiffness = if (isSwipedFarEnough) Spring.StiffnessMedium else Spring.StiffnessVeryLow
                                        ),
                                        label = "iconSpring"
                                    )

                                    val backgroundColor by animateColorAsState(
                                        targetValue = if (isSwipedFarEnough) {
                                            MaterialTheme.colorScheme.error
                                        } else MaterialTheme.colorScheme.errorContainer,
                                        label = "colorTransition"
                                    )

                                    val alignment = when (dismissState.dismissDirection) {
                                        SwipeToDismissBoxValue.StartToEnd -> Alignment.CenterStart
                                        SwipeToDismissBoxValue.EndToStart -> Alignment.CenterEnd
                                        else -> Alignment.Center
                                    }

                                    Box(
                                        modifier = Modifier.fillMaxSize(),
                                        contentAlignment = alignment
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .width(animatedWidth)
                                                .fillMaxHeight()
                                                .clip(CircleShape)
                                                .background(backgroundColor)
                                                .padding(horizontal = 24.dp),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                painter = painterResource(Res.drawable.icon_outline_rounded_delete),
                                                contentDescription = null,
                                                modifier = Modifier.scale(scale),
                                                tint = if (isSwipedFarEnough) {
                                                    MaterialTheme.colorScheme.onError
                                                } else MaterialTheme.colorScheme.onErrorContainer
                                            )
                                        }
                                    }
                                }
                            ) {
                                MedicationListItem(
                                    medicationItem = medicationItem,
                                    isSelected = state.selectedMedicationIds.contains(medicationItem.medication.medicationId),
                                    isSelectionMode = state.isSelectionMode,
                                    onClick = {
                                        if (state.isSelectionMode) {
                                            onAction(MedicationListAction.OnToggleSelection(medicationItem.medication.medicationId))
                                        } else {
                                            onAction(MedicationListAction.OnClickMedicationItem(medicationItem.medication.medicationId))
                                        }
                                    },
                                    onLongClick = {
                                        onAction(MedicationListAction.OnToggleSelection(medicationItem.medication.medicationId))
                                    },
                                    onReminderToggled = { isEnabled ->
                                        onAction(
                                            MedicationListAction.OnMedicationReminderToggled(
                                                medication = medicationItem.medication,
                                                isEnabled = isEnabled
                                            )
                                        )
                                    },
                                    modifier = Modifier.animateItem()
                                )
                            }
                        }
                    }
                }
            }
        }
    }
    
    if (state.isFilterMenuVisible) {
        FilterBottomSheet(
            selectedTimeSlot = state.selectedTimeSlot,
            onDismiss = {
                onAction(MedicationListAction.OnToggleFilterMenu)
            },
            onSelect = { timeSlot ->
                onAction(MedicationListAction.OnFilterOptionSelected(timeSlot))
                onAction(MedicationListAction.OnToggleFilterMenu)
            }
        )
    }

    if (state.showDeleteMedicationDialog) {
        ConfirmationDialog(
            config = ConfirmationDialogConfig(
                title = stringResource(Res.string.delete_medication) + "?",
                message = pluralStringResource(
                    Res.plurals.delete_medication_dialog_message,
                    state.selectedMedicationIds.size
                ),
                confirmButtonText = stringResource(Res.string.btn_delete),
                cancelButtonText = stringResource(Res.string.btn_cancel),
            ),
            onConfirm = {
                onAction(MedicationListAction.OnConfirmDelete)
            },
            onDismiss = {
                onAction(MedicationListAction.OnDismissDeleteDialog)
            }
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun MedicationListScreenPreview() {
    MedRemindTheme {
        val dummyMedication1 = Medication(
            medicationId = 1,
            medicineName = "Metformin",
            dosageStrength = "500 mg",
            medicationType = "Tablet",
            frequency = "Twice Daily",
            formattedTimes = listOf("8:00 AM", "8:00 PM"),
            formattedStartDate = "Oct 23, 2023",
            isActive = true,
            formattedEndDate = null, notes = "Take with food", hospitalName = null,
            doctorName = null, hospitalAddress = null, prescriptionImagePath = null,
            medicationImagePath = null
        )
        val dummyMedication2 = dummyMedication1.copy(
            medicationId = 2,
            medicineName = "Amoxicillin",
            dosageStrength = "250 mg",
            frequency = "Every 6 Hours",
            formattedTimes = listOf("12:00 AM"),
            formattedStartDate = "Dec 26, 2025",
            isActive = true
        )
        val dummyMedication3 = dummyMedication1.copy(
            medicationId = 3,
            medicineName = "Lisinopril",
            frequency = "Once Daily",
            formattedTimes = listOf("9:00 AM"),
            formattedStartDate = "Nov 1, 2025",
            isActive = false // Inactive state
        )
        val dummyMedication4 = dummyMedication1.copy(
            medicationId = 4,
            medicineName = "Albuterol Inhaler with a very long name",
            frequency = "As Needed",
            formattedTimes = emptyList(),
            formattedStartDate = "Nov 1, 2025",
            isActive = true
        )
        val dummyMedication5 = dummyMedication1.copy(
            medicationId = 5,
            medicineName = "Methotrexate",
            dosageStrength = "2.5 mg",
            frequency = "Weekly",
            formattedTimes = listOf("10:00 AM"),
            formattedStartDate = "Nov 1, 2025",
            isActive = true
        )
        val dummyMedication6 = dummyMedication1.copy(
            medicationId = 6,
            medicineName = "Vitamin D Supplement",
            dosageStrength = "1000 IU",
            frequency = "Monthly",
            formattedTimes = listOf("12:00 PM"),
            formattedStartDate = "Nov 1, 2025",
            isActive = true
        )
        val dummyMedication7 = dummyMedication1.copy(
            medicationId = 7,
            medicineName = "Eye Drops",
            dosageStrength = "1 drop",
            frequency = "Every 4 Hours",
            formattedTimes = listOf("8:00 AM"),
            formattedStartDate = "Dec 27, 2025",
            isActive = true
        )

        val medicationItems = listOf(
            MedicationItem(
                medication = dummyMedication1,
                formattedFrequency = UiText.DynamicString("Twice Daily"),
                nextDoseTime = "8:00 PM",
                nextDoseIn = UiText.DynamicString("Next dose in 1h 30m")
            ),
            MedicationItem(
                medication = dummyMedication2,
                formattedFrequency = UiText.DynamicString("Every 6 Hours"),
                nextDoseTime = "12:00 AM",
                nextDoseIn = UiText.DynamicString("Next dose in 5h 30m")
            ),
            MedicationItem(
                medication = dummyMedication3,
                formattedFrequency = UiText.DynamicString("Once Daily"),
                nextDoseTime = null,
                nextDoseIn = null
            ),
            MedicationItem(
                medication = dummyMedication4,
                formattedFrequency = UiText.DynamicString("As Needed"),
                nextDoseTime = "As Needed",
                nextDoseIn = null
            ),
            MedicationItem(
                medication = dummyMedication5,
                formattedFrequency = UiText.DynamicString("Weekly"),
                nextDoseTime = "10:00 AM",
                nextDoseIn = UiText.DynamicString("Next dose in 1d 3h")
            ),
            MedicationItem(
                medication = dummyMedication6,
                formattedFrequency = UiText.DynamicString("Monthly"),
                nextDoseTime = "12:00 PM",
                nextDoseIn = UiText.DynamicString("Next dose in 19d 5h")
            ),
            MedicationItem(
                medication = dummyMedication7,
                formattedFrequency = UiText.DynamicString("Every 4 Hours"),
                nextDoseTime = "8:00 AM",
                nextDoseIn = UiText.DynamicString("Next dose in 1h 30m")
            )
        )

        val dummyState = MedicationListState(
            medicationItems = medicationItems,
            searchQuery = "",
            isFilterMenuVisible = false,
            selectedTimeSlot = TimeSlot.ALL,
            isFabMenuExpanded = false,
            isLoading = false,
            eventMessage = null,
            dayOfWeek = UiText.DynamicString("SATURDAY"),
            dayOfMonth = "27",
            month = UiText.DynamicString("December"),
            year = "2025"
        )

        MedicationListScreen(
            state = dummyState,
            snackbarState = SnackbarHostState(),
            onAction = {}
        )
    }
}