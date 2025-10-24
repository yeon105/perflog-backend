import { useState } from "react";
import { useNavigate, Link } from "react-router-dom";
import { loginApi } from "../../api/auth";
import "../../styles/pages/LoginPage.css";
import toast from "react-hot-toast";

export default function LoginPage() {
  const navigate = useNavigate();

  const [form, setForm] = useState({ email: "", password: "" });
  const [submitting, setSubmitting] = useState(false);

  const onChange = (e) => {
    const { name, value } = e.target;
    setForm((prev) => ({ ...prev, [name]: value }));
  };

  const onSubmit = async (e) => {
    e.preventDefault();
    setSubmitting(true);
    try {
      const response = await loginApi({
        email: form.email,
        password: form.password,
      });
      toast.success(response.data.message);
      navigate("/", { replace: true });
    } catch (error) {
      toast.error(error.response.data.error);
    } finally {
      setSubmitting(false);
    }
  };

  return (
    <section className="login-container">
      <Link to="/" className="login-title-link">
        <h1 className="login-title">PerfLog</h1>
      </Link>

      <form onSubmit={onSubmit} className="login-form">
        <input
          name="email"
          type="email"
          placeholder="아이디 (이메일)"
          value={form.email}
          onChange={onChange}
          className="login-input"
          required
        />
        <input
          name="password"
          type="password"
          placeholder="비밀번호"
          value={form.password}
          onChange={onChange}
          className="login-input"
          required
        />

        <button type="submit" disabled={submitting} className="login-button">
          {submitting ? "로그인 중..." : "로그인"}
        </button>
      </form>

      <div className="login-links">
        <span>아이디 찾기</span> | <span>비밀번호 찾기</span> |{" "}
        <Link to="/signup">회원가입</Link>
      </div>
    </section>
  );
}
