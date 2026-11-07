export type AuthSession = {
  token: string;
  refreshToken: string;
  expiresAt: number | null;
};

type AuthResponse = {
  token: string;
  refreshToken: string;
};

type RegisteredUser = {
  email: string;
};

export class AuthApiError extends Error {
  code?: number;

  constructor(message: string, code?: number) {
    super(message);
    this.name = "AuthApiError";
    this.code = code;
  }
}

const API_BASE_URL =
  process.env.NEXT_PUBLIC_API_BASE_URL ?? "http://localhost:8080";
const SESSION_STORAGE_KEY = "corely-auth-v1";
const REMEMBERED_SESSION_TTL = 30 * 24 * 60 * 60 * 1000;

function isRecord(value: unknown): value is Record<string, unknown> {
  return typeof value === "object" && value !== null && !Array.isArray(value);
}

async function postAuthRequest(
  path: string,
  body: Record<string, string>,
): Promise<unknown> {
  const response = await fetch(`${API_BASE_URL}/api/v1/auth/${path}`, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(body),
    cache: "no-store",
  });

  let payload: unknown;
  try {
    payload = await response.json();
  } catch {
    throw new AuthApiError(
      `The authentication service returned an invalid response (${response.status}).`,
    );
  }

  if (!isRecord(payload)) {
    throw new AuthApiError("The authentication service returned an unexpected response.");
  }

  const message =
    typeof payload.message === "string" && payload.message.trim()
      ? payload.message
      : `Authentication request failed (${response.status}).`;
  const code = typeof payload.code === "number" ? payload.code : undefined;

  if (!response.ok) {
    throw new AuthApiError(message, code);
  }
  if (!("result" in payload) || payload.result == null) {
    throw new AuthApiError("The authentication service returned an unexpected response.", code);
  }

  return payload.result;
}

function readAuthResponse(value: unknown): AuthResponse {
  if (
    !isRecord(value) ||
    typeof value.token !== "string" ||
    !value.token ||
    typeof value.refreshToken !== "string" ||
    !value.refreshToken
  ) {
    throw new AuthApiError("The login response did not contain valid authentication tokens.");
  }

  return { token: value.token, refreshToken: value.refreshToken };
}

function saveAuthSession(tokens: AuthResponse, remember: boolean) {
  const storage = remember ? window.localStorage : window.sessionStorage;
  const session: AuthSession = {
    ...tokens,
    expiresAt: remember ? Date.now() + REMEMBERED_SESSION_TTL : null,
  };

  try {
    window.localStorage.removeItem(SESSION_STORAGE_KEY);
    window.sessionStorage.removeItem(SESSION_STORAGE_KEY);
    storage.setItem(SESSION_STORAGE_KEY, JSON.stringify(session));
  } catch {
    throw new Error("Đăng nhập thành công nhưng không thể lưu phiên trên trình duyệt này.");
  }
}

export async function loginWithEmail(
  email: string,
  password: string,
  remember: boolean,
): Promise<void> {
  const result = await postAuthRequest("login", { email, password });
  const tokens = readAuthResponse(result);
  saveAuthSession(tokens, remember);
}

export async function registerWithEmail(input: {
  email: string;
  password: string;
  fullName: string;
  phone: string;
}): Promise<RegisteredUser> {
  const result = await postAuthRequest("register", input);
  if (!isRecord(result) || typeof result.email !== "string") {
    throw new AuthApiError("The registration response did not contain a valid user.");
  }

  return { email: result.email };
}

export function getAuthSession(): AuthSession | undefined {
  const remembered = window.localStorage.getItem(SESSION_STORAGE_KEY);
  const storage = remembered ? window.localStorage : window.sessionStorage;
  const saved = remembered ?? window.sessionStorage.getItem(SESSION_STORAGE_KEY);
  if (!saved) return undefined;

  let value: unknown;
  try {
    value = JSON.parse(saved);
  } catch {
    storage.removeItem(SESSION_STORAGE_KEY);
    return undefined;
  }

  if (
    !isRecord(value) ||
    typeof value.token !== "string" ||
    typeof value.refreshToken !== "string" ||
    (value.expiresAt !== null && typeof value.expiresAt !== "number")
  ) {
    storage.removeItem(SESSION_STORAGE_KEY);
    return undefined;
  }

  if (value.expiresAt !== null && value.expiresAt <= Date.now()) {
    storage.removeItem(SESSION_STORAGE_KEY);
    return undefined;
  }

  return {
    token: value.token,
    refreshToken: value.refreshToken,
    expiresAt: value.expiresAt,
  };
}
