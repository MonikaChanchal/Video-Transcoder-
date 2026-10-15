import { useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import api, { errorMessage, saveSession } from "../api.js";

export default function Login() {
  const navigate = useNavigate();
  const [form, setForm] = useState({ email: "", password: "" });
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(false);

  const update = (e) => setForm({ ...form, [e.target.name]: e.target.value });

  const submit = async (e) => {
    e.preventDefault();
    setError("");
    setLoading(true);
    try {
      const { data } = await api.post("/auth/login", form);
      saveSession(data);
      navigate("/dashboard");
    } catch (err) {
      setError(errorMessage(err));
    } finally {
      setLoading(false);
    }
  };

  return (
    <main className="auth-page">
      <aside className="auth-side">
        <h1>Make every video fit where it needs to go.</h1>
        <p>Edit, transcode and optimise your videos in one place.</p>
      </aside>

      <form className="auth-card" onSubmit={submit}>
        <h2>Welcome back</h2>

        <label>Email
          <input name="email" type="email" value={form.email} onChange={update} autoComplete="email" required />
        </label>
        <label>Password
          <input name="password" type="password" value={form.password} onChange={update}
                 autoComplete="current-password" required />
        </label>

        {error && <p className="error" role="alert">{error}</p>}

        <button type="submit" disabled={loading}>{loading ? "Logging in..." : "Log in"}</button>
        <p className="switch">New here? <Link to="/register">Create an account</Link></p>
      </form>
    </main>
  );
}
