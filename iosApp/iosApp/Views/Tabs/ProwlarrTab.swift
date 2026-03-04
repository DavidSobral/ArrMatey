//
//  ProwlarrTab.swift
//  iosApp
//

import SwiftUI
import Shared

struct ProwlarrTab: View {
    @State private var selectedSegment = 0
    
    var body: some View {
        VStack(spacing: 0) {
            Picker("", selection: $selectedSegment) {
                Text(MR.strings().indexers.localized()).tag(0)
                Text("Search").tag(1)
            }
            .pickerStyle(.segmented)
            .padding(.horizontal, 16)
            .padding(.vertical, 8)
            
            if selectedSegment == 0 {
                ProwlarrIndexersView()
            } else {
                ProwlarrSearchView()
            }
        }
        .navigationTitle("Prowlarr")
    }
}
