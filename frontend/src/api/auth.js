import { http } from "./http";

export async function loginApi({ email, password }) {
  return http.post("/member/login", { email, password });
}
