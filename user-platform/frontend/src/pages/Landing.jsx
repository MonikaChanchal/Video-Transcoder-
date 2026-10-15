import { Link } from "react-router-dom";

const steps = [
  ["Edit", "Trim, crop, add a watermark or text, fix the audio."],
  ["Transcode", "Change resolution, FPS, bitrate, codec and format."],
  ["Optimise", "Get suggested settings and see how much space you save."],
  ["Automate", "Upload up to 100 videos and apply one workflow to all."],
  ["Analyse", "Read a report on size, quality and cost for every batch."],
  ["Manage", "Keep your history and clean up old files automatically."],
];

export default function Landing() {
  const loggedIn = !!localStorage.getItem("token");
  return (
    <div className="landing">
      <header className="topbar">
        <strong className="brand">Video Platform</strong>
        <nav>
          {loggedIn ? (
            <Link to="/dashboard">Go to dashboard</Link>
          ) : (
            <>
              <Link to="/login">Log in</Link>
              <Link to="/register" className="btn-link">Get started</Link>
            </>
          )}
        </nav>
      </header>

      <section className="hero">
        <h1>Make every video fit where it needs to go.</h1>
        <p>Upload a video, pick the size and quality you need, and download a file that is ready to share or publish.</p>
        <Link to={loggedIn ? "/dashboard" : "/register"} className="btn-link big">
          {loggedIn ? "Open dashboard" : "Create a free account"}
        </Link>
      </section>

      <section className="section">
        <h2>What you can do</h2>
        <dl className="steps">
          {steps.map(([t, d]) => (
            <div key={t}><dt>{t}</dt><dd>{d}</dd></div>
          ))}
        </dl>
      </section>

      <section className="section">
        <h2>Pricing</h2>
        <div className="pricing">
          <div>
            <h3>Free</h3>
            <p className="price">₹0</p>
            <ul>
              <li>Basic transcoding</li>
              <li>Resolution change</li>
              <li>5 videos a month</li>
              <li>Files up to 500 MB</li>
            </ul>
          </div>
          <div className="featured">
            <h3>Premium</h3>
            <p className="price">₹499 <span>a month</span></p>
            <ul>
              <li>Advanced transcoding and editing</li>
              <li>Batch upload of 100+ videos</li>
              <li>AI analysis and reports</li>
              <li>Templates and workflows</li>
            </ul>
          </div>
        </div>
      </section>
    </div>
  );
}
