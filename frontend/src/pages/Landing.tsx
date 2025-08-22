import { Link } from 'react-router-dom'
import { useAuth } from '../lib/auth'

const categories = [
  { name: "Maids & Helpers", icon: "🏠", description: "Full-time, cleaning, cooking maids" },
  { name: "Food Services", icon: "🍽️", description: "Home cooks, tiffin services" },
  { name: "Education", icon: "📚", description: "Tutors, music, dance teachers" },
  { name: "Tech Repairs", icon: "🔧", description: "Mobile, laptop, appliance repairs" },
  { name: "Personal Care", icon: "💆", description: "Beauticians, massage, fitness" },
  { name: "Vehicle Care", icon: "🚗", description: "Car cleaning, drivers, mechanics" },
  { name: "Events", icon: "🎉", description: "Photography, decoration, catering" },
  { name: "Home Repairs", icon: "⚡", description: "Electrical, plumbing, painting" }
]

export default function Landing() {
  const { user, token } = useAuth()
  return (
    <div className="min-h-screen bg-gradient-to-br from-blue-50 to-indigo-100">
      {/* Hero Section */}
      <section className="relative overflow-hidden">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 pt-20 pb-16">
          <div className="text-center">
            <h1 className="text-5xl md:text-6xl font-bold text-gray-900 mb-6">
              Home services at your doorstep
            </h1>
            {!token ? (
              <>
                <p className="text-xl text-gray-600 max-w-3xl mx-auto mb-8">
                  From quick repairs to construction jobs, connect with verified workers and get the job done. 
                  Trusted by thousands of customers across India.
                </p>
                <div className="flex flex-col sm:flex-row items-center justify-center gap-4 mb-12">
                  <Link 
                    to="/client" 
                    className="w-full sm:w-auto px-8 py-4 bg-blue-600 text-white rounded-lg font-semibold hover:bg-blue-700 transition-colors"
                  >
                    Post a Job
                  </Link>
                  <Link 
                    to="/worker" 
                    className="w-full sm:w-auto px-8 py-4 bg-gray-900 text-white rounded-lg font-semibold hover:bg-gray-800 transition-colors"
                  >
                    Find Gigs
                  </Link>
                </div>
                
                {/* Stats */}
                <div className="grid grid-cols-1 md:grid-cols-3 gap-8 max-w-2xl mx-auto">
                  <div className="text-center">
                    <div className="text-3xl font-bold text-blue-600">4.8</div>
                    <div className="text-sm text-gray-600">Service Rating</div>
                  </div>
                  <div className="text-center">
                    <div className="text-3xl font-bold text-blue-600">10K+</div>
                    <div className="text-sm text-gray-600">Verified Workers</div>
                  </div>
                  <div className="text-center">
                    <div className="text-3xl font-bold text-blue-600">50K+</div>
                    <div className="text-sm text-gray-600">Jobs Completed</div>
                  </div>
                </div>
              </>
            ) : (
              <>
                <p className="text-xl text-gray-600 max-w-3xl mx-auto mb-8">
                  Welcome back, {user?.name?.split(' ')[0]}! 
                  {user?.role === 'CLIENT' ? 'What service do you need today?' : 'Ready to find your next gig?'}
                </p>
                <div className="flex flex-col sm:flex-row items-center justify-center gap-4 mb-12">
                  {user?.role === 'CLIENT' ? (
                    <Link 
                      to="/client" 
                      className="w-full sm:w-auto px-8 py-4 bg-blue-600 text-white rounded-lg font-semibold hover:bg-blue-700 transition-colors"
                    >
                      Go to Dashboard
                    </Link>
                  ) : user?.role === 'WORKER' ? (
                    <Link 
                      to="/worker" 
                      className="w-full sm:w-auto px-8 py-4 bg-green-600 text-white rounded-lg font-semibold hover:bg-green-700 transition-colors"
                    >
                      Find Jobs Near You
                    </Link>
                  ) : null}
                </div>
              </>
            )}
          </div>
        </div>
      </section>

      {/* Categories Section */}
      <section className="py-16 bg-white">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <div className="text-center mb-12">
            <h2 className="text-3xl font-bold text-gray-900 mb-4">What are you looking for?</h2>
            <p className="text-gray-600">Choose from our wide range of professional services</p>
          </div>
          
          <div className="grid grid-cols-2 md:grid-cols-3 lg:grid-cols-4 gap-6">
            {categories.map((category) => (
              <Link 
                key={category.name} 
                to={token && user?.role === 'CLIENT' ? "/client" : token && user?.role === 'WORKER' ? "/worker" : "/client"}
                className="group bg-gray-50 rounded-lg p-6 text-center hover:bg-blue-50 hover:shadow-md transition-all duration-200"
              >
                <div className="text-4xl mb-3">{category.icon}</div>
                <div className="font-semibold text-gray-900 group-hover:text-blue-600">
                  {category.name}
                </div>
                <div className="text-sm text-gray-600 mt-2">
                  {category.description}
                </div>
              </Link>
            ))}
          </div>
        </div>
      </section>

      {/* How it works */}
      <section className="py-16 bg-gray-50">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <div className="text-center mb-12">
            <h2 className="text-3xl font-bold text-gray-900 mb-4">How it works</h2>
            <p className="text-gray-600">Get your job done in 3 simple steps</p>
          </div>
          
          <div className="grid grid-cols-1 md:grid-cols-3 gap-8">
            <div className="text-center">
              <div className="w-16 h-16 bg-blue-600 rounded-full flex items-center justify-center text-white text-2xl font-bold mx-auto mb-4">
                1
              </div>
              <h3 className="text-xl font-semibold mb-2">Post a Job</h3>
              <p className="text-gray-600">Describe what you need and set your budget</p>
            </div>
            <div className="text-center">
              <div className="w-16 h-16 bg-blue-600 rounded-full flex items-center justify-center text-white text-2xl font-bold mx-auto mb-4">
                2
              </div>
              <h3 className="text-xl font-semibold mb-2">Get Matched</h3>
              <p className="text-gray-600">Verified workers will accept your job</p>
            </div>
            <div className="text-center">
              <div className="w-16 h-16 bg-blue-600 rounded-full flex items-center justify-center text-white text-2xl font-bold mx-auto mb-4">
                3
              </div>
              <h3 className="text-xl font-semibold mb-2">Job Done</h3>
              <p className="text-gray-600">Pay only after the work is completed</p>
            </div>
          </div>
        </div>
      </section>
    </div>
  )
}


