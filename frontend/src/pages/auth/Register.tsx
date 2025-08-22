import { FormEvent, useState, useEffect } from 'react'
import axios from 'axios'
import { useNavigate } from 'react-router-dom'

interface FormData {
  name: string
  email: string
  phone: string
  password: string
  role: string
  locationLat?: number
  locationLng?: number
  aadhaarNumber?: string
  panNumber?: string
  address?: string
  radiusKm?: number
  verificationType?: string
}

export default function Register() {
  const navigate = useNavigate()
  const [step, setStep] = useState(1)
  const [form, setForm] = useState<FormData>({ 
    name: '', email: '', phone: '', password: '', role: 'CLIENT' 
  })
  const [error, setError] = useState<string | null>(null)
  const [ok, setOk] = useState<string | null>(null)
  const [otp, setOtp] = useState('')
  const [otpSent, setOtpSent] = useState(false)
  const [otpVerified, setOtpVerified] = useState(false)
  const [workerType, setWorkerType] = useState<'BASIC' | 'FULL' | null>(null)
  const [files, setFiles] = useState<{[key: string]: File}>({})

  const getCurrentLocation = () => {
    if (navigator.geolocation) {
      navigator.geolocation.getCurrentPosition(
        (position) => {
          setForm({
            ...form,
            locationLat: position.coords.latitude,
            locationLng: position.coords.longitude
          })
        },
        (error) => {
          console.error('Error getting location:', error)
          setError('Could not get your location. Please enable location services.')
        }
      )
    } else {
      setError('Geolocation is not supported by this browser.')
    }
  }

  const sendOtp = async () => {
    try {
      const response = await axios.post('/api/auth/send-otp', { phoneNumber: form.phone })
      setOtpSent(true)
      setOk('OTP sent to your phone: ' + response.data.otp) // Remove in production
    } catch (err: any) {
      setError('Failed to send OTP')
    }
  }

  const verifyOtp = async () => {
    try {
      await axios.post('/api/auth/verify-otp', { phoneNumber: form.phone, otp })
      setOtpVerified(true)
      setOk('Phone verified successfully!')
    } catch (err: any) {
      setError('Invalid OTP')
    }
  }

  const handleFileChange = (type: string, file: File) => {
    setFiles({ ...files, [type]: file })
  }

  const uploadFiles = async (workerId: number) => {
    const uploadPromises = Object.entries(files).map(([type, file]) => {
      const formData = new FormData()
      formData.append('file', file)
      formData.append('workerId', workerId.toString())
      formData.append('documentType', type.toUpperCase())
      
      return axios.post('/api/upload/document', formData, {
        headers: { 'Content-Type': 'multipart/form-data' }
      })
    })

    await Promise.all(uploadPromises)
  }

  const onSubmit = async (e: FormEvent) => {
    e.preventDefault()
    setError(null); setOk(null)
    
    try {
      const registrationData = {
        ...form,
        verificationType: workerType
      }
      
      const response = await axios.post('/api/auth/register', registrationData)
      
      // If worker with files, upload documents
      if (form.role === 'WORKER' && Object.keys(files).length > 0) {
        // We need to get the worker ID from the response or make another call
        // For now, we'll assume the registration response includes the worker ID
        // In a real implementation, you might need to fetch the worker profile
      }
      
      setOk('Registered successfully. Please login.')
      setTimeout(() => navigate('/login'), 1500)
    } catch (err: any) {
      setError(err?.response?.data?.message || 'Registration failed')
    }
  }

  const renderStep1 = () => (
    <div className="space-y-4">
      <h2 className="text-2xl font-semibold mb-6">Create Account - Basic Info</h2>
      <input 
        className="w-full border rounded px-3 py-2" 
        placeholder="Full name" 
        value={form.name} 
        onChange={e => setForm({ ...form, name: e.target.value })} 
        required 
      />
      <input 
        className="w-full border rounded px-3 py-2" 
        placeholder="Email" 
        type="email" 
        value={form.email} 
        onChange={e => setForm({ ...form, email: e.target.value })} 
        required 
      />
      <div className="flex gap-2">
        <input 
          className="flex-1 border rounded px-3 py-2" 
          placeholder="Phone" 
          value={form.phone} 
          onChange={e => setForm({ ...form, phone: e.target.value })} 
          required 
        />
        <button 
          type="button" 
          onClick={sendOtp}
          className="bg-green-600 text-white px-4 py-2 rounded"
          disabled={!form.phone || otpSent}
        >
          {otpSent ? 'Sent' : 'Send OTP'}
        </button>
      </div>
      
      {otpSent && !otpVerified && (
        <div className="flex gap-2">
          <input 
            className="flex-1 border rounded px-3 py-2" 
            placeholder="Enter OTP" 
            value={otp} 
            onChange={e => setOtp(e.target.value)} 
          />
          <button 
            type="button" 
            onClick={verifyOtp}
            className="bg-blue-600 text-white px-4 py-2 rounded"
          >
            Verify
          </button>
        </div>
      )}
      
      <input 
        className="w-full border rounded px-3 py-2" 
        placeholder="Password" 
        type="password" 
        value={form.password} 
        onChange={e => setForm({ ...form, password: e.target.value })} 
        required 
      />
      <select 
        className="w-full border rounded px-3 py-2" 
        value={form.role} 
        onChange={e => setForm({ ...form, role: e.target.value })}
      >
        <option value="CLIENT">Client (Post Jobs)</option>
        <option value="WORKER">Worker (Find Jobs)</option>
      </select>
      
      <button 
        type="button"
        onClick={() => setStep(2)}
        className="w-full bg-blue-600 text-white rounded px-3 py-2"
        disabled={!otpVerified}
      >
        Next
      </button>
    </div>
  )

  const renderClientStep2 = () => (
    <div className="space-y-4">
      <h2 className="text-2xl font-semibold mb-6">Client Registration - Location</h2>
      <p className="text-gray-600">We need your location to show you nearby workers.</p>
      
      <button 
        type="button"
        onClick={getCurrentLocation}
        className="w-full bg-green-600 text-white rounded px-3 py-2"
      >
        📍 Get My Current Location
      </button>
      
      {form.locationLat && form.locationLng && (
        <div className="text-sm text-green-600">
          ✅ Location captured: {form.locationLat.toFixed(4)}, {form.locationLng.toFixed(4)}
        </div>
      )}
      
      <div className="flex gap-2">
        <button 
          type="button"
          onClick={() => setStep(1)}
          className="flex-1 bg-gray-500 text-white rounded px-3 py-2"
        >
          Back
        </button>
        <button 
          type="submit"
          className="flex-1 bg-blue-600 text-white rounded px-3 py-2"
          disabled={!form.locationLat}
        >
          Complete Registration
        </button>
      </div>
    </div>
  )

  const renderWorkerTypeSelection = () => (
    <div className="space-y-4">
      <h2 className="text-2xl font-semibold mb-6">Choose Worker Registration Type</h2>
      
      <div className="grid gap-4">
        <div 
          className={`border-2 rounded-lg p-4 cursor-pointer ${workerType === 'BASIC' ? 'border-blue-500 bg-blue-50' : 'border-gray-300'}`}
          onClick={() => setWorkerType('BASIC')}
        >
          <h3 className="font-semibold text-lg">⚡ Quick Registration</h3>
          <p className="text-sm text-gray-600">Lower earnings, faster approval</p>
          <ul className="text-sm mt-2 space-y-1">
            <li>• Aadhaar number required</li>
            <li>• PAN or Driving License upload</li>
            <li>• Basic verification</li>
          </ul>
        </div>
        
        <div 
          className={`border-2 rounded-lg p-4 cursor-pointer ${workerType === 'FULL' ? 'border-blue-500 bg-blue-50' : 'border-gray-300'}`}
          onClick={() => setWorkerType('FULL')}
        >
          <h3 className="font-semibold text-lg">🏆 Full Verified</h3>
          <p className="text-sm text-gray-600">Higher earnings, complete verification</p>
          <ul className="text-sm mt-2 space-y-1">
            <li>• Selfie with Aadhaar card</li>
            <li>• Aadhaar, PAN, Driving License uploads</li>
            <li>• Address verification</li>
            <li>• Full background check</li>
          </ul>
        </div>
      </div>
      
      <div className="flex gap-2">
        <button 
          type="button"
          onClick={() => setStep(1)}
          className="flex-1 bg-gray-500 text-white rounded px-3 py-2"
        >
          Back
        </button>
        <button 
          type="button"
          onClick={() => setStep(3)}
          className="flex-1 bg-blue-600 text-white rounded px-3 py-2"
          disabled={!workerType}
        >
          Continue
        </button>
      </div>
    </div>
  )

  const renderWorkerStep3 = () => (
    <div className="space-y-4">
      <h2 className="text-2xl font-semibold mb-6">
        Worker Registration - {workerType === 'BASIC' ? 'Basic' : 'Full'} Verification
      </h2>
      
      <input 
        className="w-full border rounded px-3 py-2" 
        placeholder="Aadhaar Number" 
        value={form.aadhaarNumber || ''} 
        onChange={e => setForm({ ...form, aadhaarNumber: e.target.value })} 
        required 
      />
      
      {workerType === 'BASIC' ? (
        <div className="space-y-3">
          <div>
            <label className="block text-sm font-medium mb-1">PAN Number OR Upload Driving License</label>
            <input 
              className="w-full border rounded px-3 py-2 mb-2" 
              placeholder="PAN Number (optional if uploading DL)" 
              value={form.panNumber || ''} 
              onChange={e => setForm({ ...form, panNumber: e.target.value })} 
            />
            <input 
              type="file" 
              accept="image/*,.pdf"
              onChange={e => e.target.files?.[0] && handleFileChange('driving_license', e.target.files[0])}
              className="w-full border rounded px-3 py-2"
            />
          </div>
        </div>
      ) : (
        <div className="space-y-3">
          <input 
            className="w-full border rounded px-3 py-2" 
            placeholder="PAN Number" 
            value={form.panNumber || ''} 
            onChange={e => setForm({ ...form, panNumber: e.target.value })} 
            required 
          />
          
          <textarea 
            className="w-full border rounded px-3 py-2" 
            placeholder="Full Address" 
            value={form.address || ''} 
            onChange={e => setForm({ ...form, address: e.target.value })} 
            required 
          />
          
          <div>
            <label className="block text-sm font-medium mb-1">Selfie with Aadhaar Card</label>
            <input 
              type="file" 
              accept="image/*"
              onChange={e => e.target.files?.[0] && handleFileChange('selfie_with_aadhaar', e.target.files[0])}
              className="w-full border rounded px-3 py-2"
              required
            />
          </div>
          
          <div>
            <label className="block text-sm font-medium mb-1">Aadhaar Card</label>
            <input 
              type="file" 
              accept="image/*,.pdf"
              onChange={e => e.target.files?.[0] && handleFileChange('aadhaar_card', e.target.files[0])}
              className="w-full border rounded px-3 py-2"
              required
            />
          </div>
          
          <div>
            <label className="block text-sm font-medium mb-1">PAN Card</label>
            <input 
              type="file" 
              accept="image/*,.pdf"
              onChange={e => e.target.files?.[0] && handleFileChange('pan_card', e.target.files[0])}
              className="w-full border rounded px-3 py-2"
              required
            />
          </div>
          
          <div>
            <label className="block text-sm font-medium mb-1">Driving License</label>
            <input 
              type="file" 
              accept="image/*,.pdf"
              onChange={e => e.target.files?.[0] && handleFileChange('driving_license', e.target.files[0])}
              className="w-full border rounded px-3 py-2"
              required
            />
          </div>
        </div>
      )}
      
      <div>
        <label className="block text-sm font-medium mb-1">Service Radius (km)</label>
        <input 
          type="range" 
          min="2" 
          max="20" 
          value={form.radiusKm || 5} 
          onChange={e => setForm({ ...form, radiusKm: parseInt(e.target.value) })}
          className="w-full"
        />
        <div className="text-center text-sm text-gray-600">{form.radiusKm || 5} km</div>
      </div>
      
      <button 
        type="button"
        onClick={getCurrentLocation}
        className="w-full bg-green-600 text-white rounded px-3 py-2"
      >
        📍 Get My Current Location
      </button>
      
      {form.locationLat && form.locationLng && (
        <div className="text-sm text-green-600">
          ✅ Location captured: {form.locationLat.toFixed(4)}, {form.locationLng.toFixed(4)}
        </div>
      )}
      
      <div className="flex gap-2">
        <button 
          type="button"
          onClick={() => setStep(2)}
          className="flex-1 bg-gray-500 text-white rounded px-3 py-2"
        >
          Back
        </button>
        <button 
          type="submit"
          className="flex-1 bg-blue-600 text-white rounded px-3 py-2"
          disabled={!form.locationLat || !form.aadhaarNumber}
        >
          Complete Registration
        </button>
      </div>
    </div>
  )

  return (
    <div className="max-w-lg mx-auto px-4 py-12">
      <form onSubmit={onSubmit}>
        {step === 1 && renderStep1()}
        {step === 2 && form.role === 'CLIENT' && renderClientStep2()}
        {step === 2 && form.role === 'WORKER' && renderWorkerTypeSelection()}
        {step === 3 && form.role === 'WORKER' && renderWorkerStep3()}
        
        {error && <div className="text-sm text-red-600 mt-4">{error}</div>}
        {ok && <div className="text-sm text-green-600 mt-4">{ok}</div>}
      </form>
    </div>
  )
}


