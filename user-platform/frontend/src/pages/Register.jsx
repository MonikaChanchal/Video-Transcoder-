import { useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import api, { errorMessage, saveSession } from "../api.js";

export default function Register() {
  const navigate = useNavigate();
  const [form, setForm] = useState({
    name: "", email: "", password: "", confirmPassword: "", accountType: "PERSONAL",
  });
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(false);

  const update = (e) => setForm({ ...form, [e.target.name]: e.target.value });

  const submit = async (e) => {
    e.preventDefault();
    setError("");
    if (form.password !== form.confirmPassword) {
      setError("Passwords do not match");
      return;
    }
    setLoading(true);
    try {
      const { data } = await api.post("/auth/register", form);
      saveSession(data);
      navigate("/plans");
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
        <h2>Create your account</h2>

        <label>Name
          <input name="name" value={form.name} onChange={update} autoComplete="name" required />
        </label>
        <label>Email
          <input name="email" type="email" value={form.email} onChange={update} autoComplete="email" required />
        </label>
        <label>Password
          <input name="password" type="password" value={form.password} onChange={update}
                 autoComplete="new-password" minLength={6} required />
        </label>
        <label>Confirm password
          <input name="confirmPassword" type="password" value={form.confirmPassword}
                 onChange={update} autoComplete="new-password" required />
        </label>

        <fieldset>
          <legend>I will use it for</legend>
          <label className="choice">
            <input type="radio" name="accountType" value="PERSONAL"
                   checked={form.accountType === "PERSONAL"} onChange={update} />
            Personal use
          </label>
          <label className="choice">
            <input type="radio" name="accountType" value="ORGANIZATION"
                   checked={form.accountType === "ORGANIZATION"} onChange={update} />
            My organisation
          </label>
        </fieldset>

        {error && <p className="error" role="alert">{error}</p>}

        <button type="submit" disabled={loading}>
          {loading ? "Creating account..." : "Create account"}
        </button>
        <p className="switch">Already have an account? <Link to="/login">Log in</Link></p>
      </form>
    </main>
  );
}
