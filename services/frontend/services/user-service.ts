export type UserProfile = {
  id: string;
  email: string;
  fullName: string;
  phone?: string;
  dateOfBirth?: string;
  roles: string[];
  isActive: boolean;
  avatarUrl?: string;
};

export type UpdateUserProfile = {
  fullName: string;
  phone?: string;
  dateOfBirth?: string;
  avatarUrl?: string;
};

export class UserApiError extends Error {
  constructor(
    message: string,
    readonly status?: number,
  ) {
    super(message);
    this.name = "UserApiError";
  }
}

const API_BASE_URL =
  process.env.NEXT_PUBLIC_API_BASE_URL ?? "http://localhost:8080";

function isRecord(value: unknown): value is Record<string, unknown> {
  return typeof value === "object" && value !== null && !Array.isArray(value);
}

function readUserProfile(value: unknown): UserProfile {
  if (
    !isRecord(value) ||
    typeof value.id !== "string" ||
    typeof value.email !== "string" ||
    typeof value.fullName !== "string" ||
    typeof value.isActive !== "boolean" ||
    !Array.isArray(value.roles) ||
    !value.roles.every((role) => typeof role === "string")
  ) {
    throw new UserApiError("The user API returned an unexpected profile.");
  }

  return {
    id: value.id,
    email: value.email,
    fullName: value.fullName,
    phone: typeof value.phone === "string" ? value.phone : undefined,
    dateOfBirth:
      typeof value.dateOfBirth === "string" ? value.dateOfBirth : undefined,
    roles: value.roles,
    isActive: value.isActive,
    avatarUrl: typeof value.avatarUrl === "string" ? value.avatarUrl : undefined,
  };
}

async function requestProfile(
  token: string,
  signal?: AbortSignal,
  update?: UpdateUserProfile,
): Promise<UserProfile> {
  const response = await fetch(`${API_BASE_URL}/api/v1/users/me`, {
    method: update ? "PUT" : "GET",
    headers: {
      Authorization: `Bearer ${token}`,
      ...(update ? { "Content-Type": "application/json" } : {}),
    },
    body: update ? JSON.stringify(update) : undefined,
    cache: "no-store",
    signal,
  });

  let payload: unknown;
  try {
    payload = await response.json();
  } catch {
    throw new UserApiError(
      `The user API returned an invalid response (${response.status}).`,
      response.status,
    );
  }

  if (!response.ok) {
    const message =
      isRecord(payload) && typeof payload.message === "string"
        ? payload.message
        : `The user request failed (${response.status}).`;
    throw new UserApiError(message, response.status);
  }

  if (!isRecord(payload) || !("result" in payload)) {
    throw new UserApiError("The user API returned an unexpected response.");
  }

  return readUserProfile(payload.result);
}

export function fetchCurrentUser(
  token: string,
  signal?: AbortSignal,
): Promise<UserProfile> {
  return requestProfile(token, signal);
}

export function updateCurrentUser(
  token: string,
  profile: UpdateUserProfile,
): Promise<UserProfile> {
  return requestProfile(token, undefined, profile);
}
