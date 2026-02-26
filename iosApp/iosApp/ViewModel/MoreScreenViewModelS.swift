//
//  MoreScreenViewModelS.swift
//  iosApp
//
//  Created by Owen LeJeune on 2026-01-19.
//

import Shared
import SwiftUI

@MainActor
class MoreScreenViewModelS: ObservableObject {
    private let viewModel: MoreScreenViewModel
    
    @Published private(set) var instances: [Instance] = []
    @Published private(set) var connectionStatuses: [KotlinLong:OperationStatus] = [:]
    
    init() {
        self.viewModel = KoinBridge.shared.getMoreScreenViewModel()
        
        viewModel.instances.observeAsync { self.instances = $0 }
        viewModel.testingStatus.observeAsync { self.connectionStatuses = $0 }
    }
}
