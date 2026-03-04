//
//  ProwlarrSearchView.swift
//  iosApp
//

import SwiftUI
import Shared

struct ProwlarrSearchView: View {
    @ObservedObject private var viewModel = ProwlarrSearchViewModelS()
    @State private var queryText = ""
    @State private var showGrabConfirm = false
    @State private var grabTarget: ProwlarrSearchResult? = nil
    
    var body: some View {
        VStack(spacing: 0) {
            // Search bar
            HStack(spacing: 8) {
                TextField("Search for releases...", text: $queryText)
                    .textFieldStyle(RoundedBorderTextFieldStyle())
                    .submitLabel(.search)
                    .onSubmit {
                        if !queryText.isEmpty {
                            viewModel.performSearch(queryText)
                        }
                    }
                
                Button(action: {
                    if !queryText.isEmpty {
                        viewModel.performSearch(queryText)
                    }
                }) {
                    Image(systemName: "magnifyingglass")
                }
                .disabled(queryText.isEmpty)
                
                if !queryText.isEmpty {
                    Button(action: {
                        queryText = ""
                        viewModel.clearSearch()
                    }) {
                        Image(systemName: "xmark.circle.fill")
                            .foregroundStyle(.secondary)
                    }
                }
            }
            .padding(.horizontal, 16)
            .padding(.vertical, 8)
            
            searchContent
        }
        .alert("Grab Release?", isPresented: $showGrabConfirm, presenting: grabTarget) { result in
            Button("Grab") {
                // TODO: Implement grab action
            }
            Button("Cancel", role: .cancel) {}
        } message: { result in
            Text(result.title ?? "Unknown release")
        }
    }
    
    @ViewBuilder
    private var searchContent: some View {
        if viewModel.searchResults is ProwlarrSearchStateInitial {
            initialView
                .frame(maxWidth: .infinity, maxHeight: .infinity)
        } else if viewModel.searchResults is ProwlarrSearchStateLoading {
            ProgressView()
                .frame(maxWidth: .infinity, maxHeight: .infinity)
        } else if viewModel.searchResults is ProwlarrSearchStateError {
            if let error = viewModel.searchResults as? ProwlarrSearchStateError {
                errorView(message: error.message)
            }
        } else if viewModel.searchResults is ProwlarrSearchStateSuccess {
            if let success = viewModel.searchResults as? ProwlarrSearchStateSuccess {
                if success.items.isEmpty {
                    emptyView
                        .frame(maxWidth: .infinity, maxHeight: .infinity)
                } else {
                    ScrollView {
                        VStack(spacing: 12) {
                            ForEach(Array(success.items.enumerated()), id: \.element.guid) { _, result in
                                SearchResultRow(
                                    result: result,
                                    onGrab: {
                                        grabTarget = result
                                        showGrabConfirm = true
                                    }
                                )
                            }
                        }
                        .padding(.vertical, 12)
                        .padding(.horizontal, 16)
                    }
                }
            }
        } else {
            EmptyView()
        }
    }
    
    @ViewBuilder
    private var initialView: some View {
        VStack(spacing: 12) {
            Image(systemName: "magnifyingglass.circle")
                .font(.system(size: 64))
                .foregroundStyle(.secondary)
            Text("Search for releases across your indexers")
                .font(.system(size: 17))
                .foregroundStyle(.secondary)
                .multilineTextAlignment(.center)
                .padding(.horizontal, 24)
        }
    }
    
    @ViewBuilder
    private var emptyView: some View {
        VStack(spacing: 12) {
            Image(systemName: "magnifyingglass")
                .font(.system(size: 64))
                .foregroundStyle(.secondary)
            Text("No results found")
                .font(.system(size: 17))
                .foregroundStyle(.secondary)
        }
    }
    
    private func errorView(message: String) -> some View {
        VStack(spacing: 12) {
            Image(systemName: "exclamationmark.triangle")
                .font(.system(size: 48))
                .foregroundStyle(.red)
            Text(message)
                .font(.system(size: 15))
                .foregroundStyle(.red)
                .multilineTextAlignment(.center)
                .padding(.horizontal, 24)
        }
        .frame(maxWidth: .infinity, maxHeight: .infinity)
    }
}

struct SearchResultRow: View {
    let result: ProwlarrSearchResult
    let onGrab: () -> Void
    
    var body: some View {
        VStack(alignment: .leading, spacing: 8) {
            // Title row with grab button
            HStack(alignment: .top) {
                Text(result.title ?? "Unknown")
                    .font(.system(size: 15, weight: .semibold))
                    .lineLimit(2)
                
                Spacer()
                
                Button(action: onGrab) {
                    Image(systemName: "arrow.down.circle")
                        .font(.system(size: 22))
                }
            }
            
            // Meta row
            HStack(spacing: 8) {
                Text(result.indexer ?? "Unknown")
                    .font(.caption)
                    .foregroundStyle(.secondary)
                
                Text("•")
                    .font(.caption)
                    .foregroundStyle(.secondary)
                
                // Protocol
                let protoName = result.protocol != nil ? String(describing: result.protocol!) : "Unknown"
                Text(protoName)
                    .font(.caption)
                    .foregroundStyle(protocolColor(for: result.protocol))
                
                Text("•")
                    .font(.caption)
                    .foregroundStyle(.secondary)
                
                // Size
                Text(ByteCountFormatter.string(fromByteCount: result.size, countStyle: .binary))
                    .font(.caption)
                    .foregroundStyle(.secondary)
                
                Text("•")
                    .font(.caption)
                    .foregroundStyle(.secondary)
                
                // Age
                Text("\(result.age)d")
                    .font(.caption)
                    .foregroundStyle(.secondary)
            }
            
            // Seeders/leechers for torrents
            if result.protocol == ReleaseProtocol.torrent {
                HStack(spacing: 12) {
                    HStack(spacing: 2) {
                        Image(systemName: "arrow.up")
                            .font(.caption)
                            .foregroundStyle(.green)
                        Text("\(result.seeders ?? 0)")
                            .font(.caption)
                            .foregroundStyle(.green)
                    }
                    
                    HStack(spacing: 2) {
                        Image(systemName: "arrow.down")
                            .font(.caption)
                            .foregroundStyle(.red)
                        Text("\(result.leechers ?? 0)")
                            .font(.caption)
                            .foregroundStyle(.red)
                    }
                }
            }
            
            // Categories
            if !result.categories.isEmpty {
                FlowLayout(spacing: 4) {
                    ForEach(Array(result.categories.prefix(3).enumerated()), id: \.element.id) { _, category in
                        Text(category.name ?? "Category \(category.id)")
                            .font(.caption2)
                            .padding(.horizontal, 6)
                            .padding(.vertical, 2)
                            .background(Color(.systemGray5))
                            .cornerRadius(4)
                    }
                }
            }
        }
        .padding(12)
        .background(Color(.systemGray6))
        .cornerRadius(8)
    }
    
    private func protocolColor(for proto: ReleaseProtocol?) -> Color {
        guard let proto = proto else { return .gray }
        switch proto {
        case .torrent: return .blue
        case .usenet: return .green
        default: return .gray
        }
    }
}

// Simple flow layout for categories
struct FlowLayout: Layout {
    var spacing: CGFloat = 4
    
    func sizeThatFits(proposal: ProposedViewSize, subviews: Subviews, cache: inout ()) -> CGSize {
        let result = FlowResult(in: proposal.width ?? 0, subviews: subviews, spacing: spacing)
        return result.size
    }
    
    func placeSubviews(in bounds: CGRect, proposal: ProposedViewSize, subviews: Subviews, cache: inout ()) {
        let result = FlowResult(in: bounds.width, subviews: subviews, spacing: spacing)
        for (index, subview) in subviews.enumerated() {
            subview.place(at: CGPoint(x: bounds.minX + result.positions[index].x,
                                      y: bounds.minY + result.positions[index].y),
                         proposal: .unspecified)
        }
    }
    
    struct FlowResult {
        var size: CGSize = .zero
        var positions: [CGPoint] = []
        
        init(in maxWidth: CGFloat, subviews: Subviews, spacing: CGFloat) {
            var x: CGFloat = 0
            var y: CGFloat = 0
            var rowHeight: CGFloat = 0
            
            for subview in subviews {
                let size = subview.sizeThatFits(.unspecified)
                if x + size.width > maxWidth, x > 0 {
                    x = 0
                    y += rowHeight + spacing
                    rowHeight = 0
                }
                positions.append(CGPoint(x: x, y: y))
                rowHeight = max(rowHeight, size.height)
                x += size.width + spacing
            }
            
            self.size = CGSize(width: maxWidth, height: y + rowHeight)
        }
    }
}
