//
//  CalendarMonthView.swift
//  iosApp
//
//  Created by Owen LeJeune on 2026-02-09.
//

import Shared
import SwiftUI

struct CalendarMonthView: View {
    let state: CalendarState
    
    @State private var currentMonth: Date
    @State private var selectedDate: LocalDate
    
    private var isCurrentMonth: Bool {
        Calendar.current.isDate(currentMonth, equalTo: Date(), toGranularity: .month)
    }
        
    init(state: CalendarState) {
        self.state = state
        let today = Date()
        _currentMonth = State(initialValue: today)
        _selectedDate = State(initialValue: state.today)
    }
    
    var body: some View {
        VStack(spacing: 0) {
            HStack {
                Button(action: previousMonth) {
                    Image(systemName: "chevron.left")
                        .font(.title3)
                }
                
                Spacer()
                
                Button(action: {
                    if !isCurrentMonth {
                        currentMonth = Date()
                    }
                }) {
                    Text(monthYearString)
                        .font(.title2)
                        .fontWeight(.semibold)
                        .foregroundColor(isCurrentMonth ? .themePrimary : .primary)
                }
                
                Spacer()
                
                Button(action: nextMonth) {
                    Image(systemName: "chevron.right")
                        .font(.title3)
                }
            }
            .padding()
            
            CalendarMonthGrid(currentMonth: currentMonth, selectedDate: selectedDate, onDateSelected: { date in
                selectedDate = date
            }, state: state)
            
            Divider().padding(.vertical, 8)
            
            if isDateInCurrentMonth(selectedDate) {
                ScrollView {
                    selectedDateContent
                        .padding()
                }
            } else {
                Spacer()
            }
        }
        .onChange(of: currentMonth) { _, _ in
            selectedDate = state.today
        }
    }
    
    @ViewBuilder
    private var selectedDateContent: some View {
        let dayMovies = state.movies[selectedDate] ?? []
        let dayEpisodeGroups = state.groupedEpisodes[selectedDate] ?? []
        let dayAlbums = state.albums[selectedDate] ?? []
        let dayBooks = state.books[selectedDate] ?? []
        
        CalendarDaySection(
            date: selectedDate,
            movies: dayMovies,
            episodeGroups: dayEpisodeGroups,
            albums: dayAlbums,
            books: dayBooks,
            isToday: selectedDate.isEqual(state.today)
        )
    }

    private var monthYearString: String {
        let formatter = DateFormatter()
        formatter.dateFormat = "MMMM yyyy"
        return formatter.string(from: currentMonth)
    }

    private func isDateInCurrentMonth(_ date: LocalDate) -> Bool {
        let components = Calendar.current.dateComponents([.year, .month], from: currentMonth)
        return Int(date.year) == components.year && Int(date.month.number()) == components.month
    }

    private func previousMonth() {
        if let newMonth = Calendar.current.date(byAdding: .month, value: -1, to: currentMonth) {
            currentMonth = newMonth
        }
    }

    private func nextMonth() {
        if let newMonth = Calendar.current.date(byAdding: .month, value: 1, to: currentMonth) {
            currentMonth = newMonth
        }
    }
}
