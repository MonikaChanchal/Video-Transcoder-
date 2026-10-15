import { useEffect, useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import api, { clearSession } from "../api.js";

function formatBytes(n) {
  if (!n) return "0 B";
  const units = ["B", "KB", "MB", "GB", "TB"];
  const i = Math.min(Math.floor(Math.log(n) / Math.log(1024)), units.length - 1);
  return `${(n / 1024 ** i).toFixed(i ? 1 : 0)} ${units[i]}`;
}

const menu = ["My videos", "Edit video", "Transcode", "Templates", "AI analysis", "Settings"];

export default function Dashboard() {
  const navigate = useNavigate();
  const [data, setData] = useState(null);
  const [failed, setFailed] = useState(false);

  useEffect(() => {
    api.get("/dashboard")
      .then(({ data }) => setData(data))
      .catch((err) => {
        if (err.response?.status === 401) {
          clearSession();
          navigate("/login");
        } else {
          setFailed(true);
        }
      });
  }, [navigate]);

  const logout = () => {
    clearSession();
    navigate("/login");
  };

  if (failed) return <main className="page"><p className="error">Could not load the dashboard. Is the backend running?</p></main>;
  if (!data) return null;

  const premium = data.plan === "PREMIUM";

  return (
    <div className="dash">
      <aside className="side">
        <strong className="brand">Video Platform</strong>
        <nav>
          <Link to="/dashboard" className="active">Dashboard</Link>
          {menu.map((m) => (
            <span key={m} className="disabled" title="Added by another feature">{m}</span>
          ))}
        </nav>
        <button className="secondary" onClick={logout}>Log out</button>
      </aside>

      <main className="main">
        <div className="head">
          <h1>Welcome, {data.name}</h1>
          <span className={premium ? "badge premium" : "badge"}>{data.plan}</span>
        </div>

        {!premium && (
          <div className="upgrade">
            <span>You are on the Free plan: {data.monthlyLimit} videos a month.</span>
            <Link to="/payment" className="btn-link">Upgrade to Premium</Link>
          </div>
        )}
        {premium && data.planExpiry && (
          <p className="muted">Premium is active until {new Date(data.planExpiry).toLocaleDateString()}.</p>
        )}

        <div className="stats">
          <div><span>Total videos</span><strong>{data.totalVideos}</strong></div>
          <div><span>Storage used</span><strong>{formatBytes(data.storageUsedBytes)}</strong></div>
          <div><span>This month</span><strong>{data.videosThisMonth} / {data.monthlyLimit}</strong></div>
          <div><span>Storage saved</span><strong>{formatBytes(data.storageSavedBytes)}</strong></div>
        </div>

        <h2>Recent videos</h2>
        {data.recent.length === 0 ? (
          <p className="muted">No videos yet. Uploaded and transcoded videos will appear here.</p>
        ) : (
          <div className="table-wrap">
            <table>
              <thead>
                <tr><th>Video</th><th>Output size</th><th>Resolution</th><th>Status</th><th>Date</th></tr>
              </thead>
              <tbody>
                {data.recent.map((v) => (
                  <tr key={v.id}>
                    <td>{v.originalName}</td>
                    <td>{v.outputSize ? formatBytes(v.outputSize) : "-"}</td>
                    <td>{v.resolution || "-"}</td>
                    <td>{v.status}</td>
                    <td>{new Date(v.createdAt).toLocaleDateString()}</td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}
      </main>
    </div>
  );
}
