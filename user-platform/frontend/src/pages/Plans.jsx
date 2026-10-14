import { useNavigate } from "react-router-dom";

export default function Plans() {
  const navigate = useNavigate();
  const user = JSON.parse(localStorage.getItem("user") || "{}");
  const isPremium = user.subscriptionType === "PREMIUM";

  return (
    <main className="page narrow">
      <h1>Choose your plan</h1>
      <p className="muted">You can upgrade any time from the dashboard.</p>

      <div className="pricing">
        <div>
          <h3>Free</h3>
          <p className="price">₹0</p>
          <ul>
            <li>Limited uploads (5 a month)</li>
            <li>Basic transcoding</li>
            <li>Resolution change</li>
            <li>Files up to 500 MB</li>
          </ul>
          <button className="secondary" onClick={() => navigate("/dashboard")}>
            {isPremium ? "Go to dashboard" : "Continue with Free"}
          </button>
        </div>

        <div className="featured">
          <h3>Premium</h3>
          <p className="price">₹499 <span>a month</span></p>
          <ul>
            <li>Unlimited-style quota (1000 a month)</li>
            <li>Advanced transcoding and editing</li>
            <li>Batch processing of 100+ videos</li>
            <li>AI analysis, templates and reports</li>
          </ul>
          <button onClick={() => navigate("/payment")} disabled={isPremium}>
            {isPremium ? "Your current plan" : "Upgrade to Premium"}
          </button>
        </div>
      </div>
    </main>
  );
}
