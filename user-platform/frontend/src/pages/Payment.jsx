import { useState } from "react";
import { useNavigate } from "react-router-dom";
import api, { errorMessage } from "../api.js";
import "../payment-demo.css";

function loadRazorpay() {
  return new Promise((resolve, reject) => {
    if (window.Razorpay) return resolve();
    const s = document.createElement("script");
    s.src = "https://checkout.razorpay.com/v1/checkout.js";
    s.onload = resolve;
    s.onerror = () => reject(new Error("Could not load the payment window. Check your internet connection."));
    document.body.appendChild(s);
  });
}

export default function Payment() {
  const navigate = useNavigate();
  const user = JSON.parse(localStorage.getItem("user") || "{}");
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(false);
  const [demo, setDemo] = useState(false);
  const [method, setMethod] = useState("UPI");

  const finish = (data) => {
    localStorage.setItem("user", JSON.stringify(data));
    navigate("/dashboard");
  };

  const confirmDemo = async () => {
    setError("");
    setLoading(true);
    try {
      const { data } = await api.post("/payment/demo-confirm");
      finish(data);
    } catch (err) {
      setError(errorMessage(err));
      setLoading(false);
    }
  };

  const pay = async () => {
    setError("");
    setLoading(true);
    try {
      const { data: order } = await api.post("/payment/create-order");
      if (order.demo) {          // backend demo-mode: Razorpay nahi chahiye
        setDemo(true);
        setLoading(false);
        return;
      }
      await loadRazorpay();
      const rzp = new window.Razorpay({
        key: order.keyId,
        amount: order.amount,
        currency: order.currency,
        order_id: order.orderId,
        name: "Video Platform",
        description: "Premium plan, 30 days",
        prefill: { name: user.name, email: user.email },
        handler: async (res) => {
          try {
            const { data } = await api.post("/payment/verify", {
              razorpayOrderId: res.razorpay_order_id,
              razorpayPaymentId: res.razorpay_payment_id,
              razorpaySignature: res.razorpay_signature,
            });
            finish(data);
          } catch (err) {
            setError(errorMessage(err));
            setLoading(false);
          }
        },
        modal: { ondismiss: () => setLoading(false) },
      });
      rzp.on("payment.failed", (r) => {
        setError(r.error?.description || "Payment failed. Try again.");
        setLoading(false);
      });
      rzp.open();
    } catch (err) {
      setError(errorMessage(err));
      setLoading(false);
    }
  };

  return (
    <main className="page narrow">
      <h1>Upgrade to Premium</h1>
      <dl className="summary">
        <dt>Plan</dt><dd>Premium, 30 days</dd>
        <dt>Amount</dt><dd>₹499</dd>
        <dt>Pay with</dt><dd>UPI, card, net banking or wallet</dd>
      </dl>

      {error && <p className="error" role="alert">{error}</p>}

      {demo ? (
        <div className="demo-box">
          <p className="demo-note">Demo payment. No real money is charged.</p>
          <fieldset>
            <legend>Choose payment method</legend>
            {["UPI", "Card", "Net banking", "Wallet"].map((m) => (
              <label className="choice" key={m}>
                <input type="radio" name="method" checked={method === m} onChange={() => setMethod(m)} />
                {m}
              </label>
            ))}
          </fieldset>
          <div className="row">
            <button onClick={confirmDemo} disabled={loading}>{loading ? "Processing..." : "Pay ₹499 (demo)"}</button>
            <button className="secondary" onClick={() => setDemo(false)} disabled={loading}>Cancel</button>
          </div>
        </div>
      ) : (
        <div className="row">
          <button onClick={pay} disabled={loading}>{loading ? "Please wait..." : "Pay ₹499"}</button>
          <button className="secondary" onClick={() => navigate("/plans")}>Back to plans</button>
        </div>
      )}
    </main>
  );
}
