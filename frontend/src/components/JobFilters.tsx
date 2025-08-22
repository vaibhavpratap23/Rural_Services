import { useState } from 'react'

interface JobFiltersProps {
  onFilterChange: (filters: JobFilters) => void
  categories: string[]
}

export interface JobFilters {
  category: string
  minBudget: number
  maxBudget: number
  maxDistance: number
  sortBy: 'date' | 'budget' | 'distance'
  sortOrder: 'asc' | 'desc'
}

export default function JobFilters({ onFilterChange, categories }: JobFiltersProps) {
  const [filters, setFilters] = useState<JobFilters>({
    category: '',
    minBudget: 0,
    maxBudget: 50000,
    maxDistance: 10,
    sortBy: 'date',
    sortOrder: 'desc'
  })

  const [showFilters, setShowFilters] = useState(false)

  const updateFilters = (newFilters: Partial<JobFilters>) => {
    const updated = { ...filters, ...newFilters }
    setFilters(updated)
    onFilterChange(updated)
  }

  return (
    <div className="bg-white rounded-lg shadow-sm border p-4 mb-6">
      <div className="flex items-center justify-between mb-4">
        <h3 className="text-lg font-medium">Filter Jobs</h3>
        <button
          onClick={() => setShowFilters(!showFilters)}
          className="md:hidden px-3 py-1 text-sm bg-blue-600 text-white rounded"
        >
          {showFilters ? 'Hide' : 'Show'} Filters
        </button>
      </div>

      <div className={`space-y-4 ${showFilters ? 'block' : 'hidden md:block'}`}>
        {/* Category Filter */}
        <div>
          <label className="block text-sm font-medium mb-2">Category</label>
          <select
            value={filters.category}
            onChange={(e) => updateFilters({ category: e.target.value })}
            className="w-full p-2 border rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500"
          >
            <option value="">All Categories</option>
            {categories.map(category => (
              <option key={category} value={category}>{category}</option>
            ))}
          </select>
        </div>

        {/* Budget Range */}
        <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
          <div>
            <label className="block text-sm font-medium mb-2">Min Budget (₹)</label>
            <input
              type="number"
              value={filters.minBudget}
              onChange={(e) => updateFilters({ minBudget: parseInt(e.target.value) || 0 })}
              className="w-full p-2 border rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500"
              min="0"
            />
          </div>
          <div>
            <label className="block text-sm font-medium mb-2">Max Budget (₹)</label>
            <input
              type="number"
              value={filters.maxBudget}
              onChange={(e) => updateFilters({ maxBudget: parseInt(e.target.value) || 50000 })}
              className="w-full p-2 border rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500"
              min="0"
            />
          </div>
        </div>

        {/* Distance Filter */}
        <div>
          <label className="block text-sm font-medium mb-2">
            Max Distance: {filters.maxDistance} km
          </label>
          <input
            type="range"
            min="1"
            max="50"
            value={filters.maxDistance}
            onChange={(e) => updateFilters({ maxDistance: parseInt(e.target.value) })}
            className="w-full h-2 bg-gray-200 rounded-lg appearance-none cursor-pointer"
          />
          <div className="flex justify-between text-xs text-gray-500 mt-1">
            <span>1 km</span>
            <span>50 km</span>
          </div>
        </div>

        {/* Sort Options */}
        <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
          <div>
            <label className="block text-sm font-medium mb-2">Sort By</label>
            <select
              value={filters.sortBy}
              onChange={(e) => updateFilters({ sortBy: e.target.value as 'date' | 'budget' | 'distance' })}
              className="w-full p-2 border rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500"
            >
              <option value="date">Date Posted</option>
              <option value="budget">Budget</option>
              <option value="distance">Distance</option>
            </select>
          </div>
          <div>
            <label className="block text-sm font-medium mb-2">Order</label>
            <select
              value={filters.sortOrder}
              onChange={(e) => updateFilters({ sortOrder: e.target.value as 'asc' | 'desc' })}
              className="w-full p-2 border rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500"
            >
              <option value="desc">High to Low</option>
              <option value="asc">Low to High</option>
            </select>
          </div>
        </div>

        {/* Clear Filters */}
        <button
          onClick={() => updateFilters({
            category: '',
            minBudget: 0,
            maxBudget: 50000,
            maxDistance: 10,
            sortBy: 'date',
            sortOrder: 'desc'
          })}
          className="w-full px-4 py-2 text-sm text-gray-600 border border-gray-300 rounded-lg hover:bg-gray-50"
        >
          Clear All Filters
        </button>
      </div>
    </div>
  )
}
