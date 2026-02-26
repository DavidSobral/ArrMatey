//
//  ArrHealthCard.swift
//  iosApp
//
//  Created by Owen LeJeune on 2026-02-20.
//

import Shared
import SwiftUI

struct ArrHealthCard: View {
    let health: ArrHealth
    
    private var backgroundColor: Color {
        switch health.type {
        case .error:
            return Color(UIColor.systemRed).opacity(0.2)
        case .warning:
            return Color(red: 1.0, green: 0.78, blue: 0.33)
        default:
            return Color(UIColor.secondarySystemBackground)
        }
    }
    
    private var contentColor: Color {
        switch health.type {
        case .error:
            return .red
        case .warning:
            return .black
        default:
            return .primary
        }
    }
    
    private var iconName: String {
        switch health.type {
        case .error: return "exclamationmark.octagon"
        case .warning: return "exclamationmark.triangle"
        default: return "info.circle"
        }
    }

    var body: some View {
        Button {
            if let wikiUrl = health.wikiUrl, let url = URL(string: wikiUrl) {
                UIApplication.shared.open(url)
            }
        } label: {
            HStack(spacing: 12) {
                Image(systemName: iconName)
                    .font(.system(size: 20, weight: .medium))
                
                Text(health.message ?? MR.strings().unknown.localized())
                    .font(.subheadline)
                    .frame(maxWidth: .infinity, alignment: .leading)
                    .multilineTextAlignment(.leading)
                
                if health.wikiUrl != nil {
                    Image(systemName: "book.pages")
                        .font(.system(size: 20, weight: .medium))
                }
            }
            .padding()
            .background(backgroundColor)
            .foregroundColor(contentColor)
            .cornerRadius(12)
        }
        .buttonStyle(.plain)
        .disabled(health.wikiUrl == nil)
    }
}
