
import { useState } from 'react'
import './App.css'

const API = 'http://localhost:8080/api'

function App() {
  const [email, setEmail] = useState('')
  const [password, setPassword] = useState('')
  const [user, setUser] = useState(null)
  const [token, setToken] = useState('')
  const [pickup, setPickup] = useState('')
  const [destination, setDestination] = useState('')
  const [rides, setRides] = useState([])
  const [availableRides, setAvailableRides] = useState([])
  const [message, setMessage] = useState('')
  const [loading, setLoading] = useState(false)

  async function request(url, options = {}) {
    const response = await fetch(`${API}${url}`, {
      ...options,
      headers: {
        ...(options.body ? { 'Content-Type': 'application/json' } : {}),
        ...(token ? { Authorization: `Bearer ${token}` } : {}),
        ...options.headers,
      },
    })

    if (!response.ok) {
      const errorText = await response.text()
      throw new Error(errorText || `Request failed (${response.status})`)
    }

    if (response.status === 204) return null
    return response.json()
  }

  async function login(event) {
    event.preventDefault()
    setLoading(true)
    setMessage('')

    try {
      const response = await fetch(`${API}/auth/login`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ email, password }),
      })

      if (!response.ok) {
        throw new Error('Login failed. Check your email and password.')
      }

      const data = await response.json()
      setToken(data.token)
      setUser(data)
      setMessage(`Welcome, ${data.name}!`)
    } catch (error) {
      setMessage(error.message || 'Cannot connect to backend.')
    } finally {
      setLoading(false)
    }
  }

  async function loadRides() {
    setLoading(true)
    setMessage('')

    try {
      const data = user.role === 'DRIVER'
        ? await request('/rides/driver/mine')
        : await request('/rides/mine')

      setRides(data)
      setMessage('Ride history loaded.')
    } catch (error) {
      setMessage(error.message)
    } finally {
      setLoading(false)
    }
  }

  async function bookRide(event) {
    event.preventDefault()

    if (!pickup.trim() || !destination.trim()) {
      setMessage('Enter both pickup and destination.')
      return
    }

    setLoading(true)
    setMessage('')

    try {
      await request('/rides', {
        method: 'POST',
        body: JSON.stringify({
          pickupLocation: pickup.trim(),
          dropLocation: destination.trim(),
        }),
      })

      setPickup('')
      setDestination('')
      setMessage('Ride requested successfully!')
      await loadRides()
    } catch (error) {
      setMessage(error.message)
    } finally {
      setLoading(false)
    }
  }

  async function loadAvailableRides() {
    setLoading(true)
    setMessage('')

    try {
      const data = await request('/rides/available')
      setAvailableRides(data)
      setMessage('Available rides loaded.')
    } catch (error) {
      setMessage(error.message)
    } finally {
      setLoading(false)
    }
  }

  async function acceptRide(rideId) {
    setLoading(true)
    setMessage('')

    try {
      await request(`/rides/${rideId}/accept`, { method: 'POST' })
      setMessage(`Ride #${rideId} accepted!`)
      await loadAvailableRides()
      await loadRides()
    } catch (error) {
      setMessage(error.message)
    } finally {
      setLoading(false)
    }
  }

  function logout() {
    setUser(null)
    setToken('')
    setRides([])
    setAvailableRides([])
    setPassword('')
    setMessage('You have logged out.')
  }

  return (
    <div className="app">
      <header className="navbar">
        <a className="brand" href="#">Ride<span>Flow</span></a>
        <nav>
          <a href="#book">Book a Ride</a>
          {user && <a href="#history">Ride History</a>}
          <a href="#login">{user ? user.name : 'Login'}</a>
        </nav>
      </header>

      <main>
        <section className="hero">
          <div className="hero-content">
            <p className="eyebrow">YOUR RIDE, YOUR WAY</p>
            <h1>Every journey<br />starts with <span>RideFlow.</span></h1>
            <p className="subtitle">
              Book your cab with ease. Travel comfortably.
              Let us take you where you need to go.
            </p>
            <a className="primary-button" href="#book">Book your ride</a>
          </div>

          <div className="hero-card">
            <div className="car-icon">🚕</div>
            <h2>Your city. Your journey.</h2>
            <p>Reliable rides, simple booking.</p>
            <div className="route">
              <span className="route-dot green"></span>
              <div><small>PICKUP</small><strong>Your location</strong></div>
            </div>
            <div className="route">
              <span className="route-dot orange"></span>
              <div><small>DESTINATION</small><strong>Your destination</strong></div>
            </div>
          </div>
        </section>

        <section className="booking-section" id="book">
          <div>
            <p className="eyebrow">QUICK AND EASY</p>
            <h2>Where would you like to go?</h2>
            <p>
              {user
                ? `Signed in as ${user.role}.`
                : 'Sign in first to request a ride.'}
            </p>
          </div>

          <form className="booking-form" onSubmit={bookRide}>
            <label>
              Pickup location
              <input
                value={pickup}
                onChange={(event) => setPickup(event.target.value)}
                placeholder="Enter pickup location"
                required
              />
            </label>

            <label>
              Destination
              <input
                value={destination}
                onChange={(event) => setDestination(event.target.value)}
                placeholder="Where are you going?"
                required
              />
            </label>

            <button
              className="primary-button"
              type="submit"
              disabled={!user || loading || user.role !== 'RIDER'}
            >
              {loading ? 'Please wait...' : 'Request a Ride'}
            </button>
          </form>
        </section>

        <section className="login-section" id="login">
          {!user ? (
            <>
              <p className="eyebrow">YOUR ACCOUNT</p>
              <h2>Login to RideFlow</h2>
              <form className="booking-form login-form" onSubmit={login}>
                <label>
                  Email
                  <input
                    type="email"
                    value={email}
                    onChange={(event) => setEmail(event.target.value)}
                    placeholder="Enter your email"
                    required
                  />
                </label>
                <label>
                  Password
                  <input
                    type="password"
                    value={password}
                    onChange={(event) => setPassword(event.target.value)}
                    placeholder="Enter your password"
                    required
                  />
                </label>
                <button className="primary-button" disabled={loading}>
                  {loading ? 'Signing in...' : 'Login'}
                </button>
              </form>
              <p>Use an existing test account to sign in.</p>
            </>
          ) : (
            <>
              <p className="eyebrow">ACCOUNT ACTIVE</p>
              <h2>Welcome, {user.name}</h2>
              <p>{user.email} · {user.role}</p>
              <div className="account-actions">
                <button className="primary-button" onClick={loadRides} disabled={loading}>
                  Load Ride History
                </button>
                {user.role === 'DRIVER' && (
                  <button className="primary-button" onClick={loadAvailableRides} disabled={loading}>
                    Find Available Rides
                  </button>
                )}
                <button className="secondary-button" onClick={logout}>
                  Logout
                </button>
              </div>
            </>
          )}

          {message && <p className="message" role="status">{message}</p>}
        </section>

        {user?.role === 'DRIVER' && availableRides.length > 0 && (
          <section className="features-section">
            <h2>Available Rides</h2>
            <div className="ride-list">
              {availableRides.map((ride) => (
                <article className="ride-card" key={ride.id}>
                  <h3>Ride #{ride.id}</h3>
                  <p>{ride.pickupLocation} → {ride.dropLocation}</p>
                  <p>Fare: ₹{ride.fare} · {ride.status}</p>
                  <button
                    className="primary-button"
                    onClick={() => acceptRide(ride.id)}
                    disabled={loading}
                  >
                    Accept Ride
                  </button>
                </article>
              ))}
            </div>
          </section>
        )}

        {user && (
          <section className="features-section" id="history">
            <p className="eyebrow">YOUR JOURNEYS</p>
            <h2>Ride History</h2>
            {rides.length === 0 ? (
              <p>No rides loaded yet. Select Load Ride History.</p>
            ) : (
              <div className="ride-list">
                {rides.map((ride) => (
                  <article className="ride-card" key={ride.id}>
                    <h3>Ride #{ride.id}</h3>
                    <p>{ride.pickupLocation} → {ride.dropLocation}</p>
                    <p>Fare: ₹{ride.fare}</p>
                    <p>Status: <strong>{ride.status}</strong></p>
                  </article>
                ))}
              </div>
            )}
          </section>
        )}

        <section className="features-section" id="features">
          <p className="eyebrow">WHY RIDEFLOW?</p>
          <h2>Travel with confidence</h2>
          <div className="feature-grid">
            <article><span>⚡</span><h3>Easy booking</h3><p>Request a ride in a few simple steps.</p></article>
            <article><span>🛡️</span><h3>Secure access</h3><p>Login with role-based rider and driver access.</p></article>
            <article><span>📍</span><h3>Ride history</h3><p>View rides recorded by the backend.</p></article>
          </div>
        </section>
      </main>

      <footer>© 2026 RideFlow · Cab Booking & Management System</footer>
    </div>
  )
}

export default App