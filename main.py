import os
import firebase_admin

from fastapi import FastAPI, Depends, HTTPException, Request
from fastapi.security import HTTPBearer, HTTPAuthorizationCredentials
from firebase_admin import auth, credentials
from slowapi import Limiter
from slowapi.util import get_remote_address
from slowapi.errors import RateLimitExceeded
from slowapi import _rate_limit_exceeded_handler
from dotenv import load_dotenv

load_dotenv()

# -------------------------
# NOVA SECURITY CONFIG
# -------------------------

limiter = Limiter(key_func=get_remote_address)

app = FastAPI(
    title="NOVA Ultimate Jarvis API",
    version="1.0.0"
)

app.state.limiter = limiter
app.add_exception_handler(
    RateLimitExceeded,
    _rate_limit_exceeded_handler
)

security = HTTPBearer(auto_error=False)

# -------------------------
# FIREBASE INITIALIZATION
# -------------------------

firebase_ready = False

service_account = os.getenv("FIREBASE_SERVICE_ACCOUNT")

if service_account and os.path.isfile(service_account):
    try:
        if not firebase_admin._apps:
            cred = credentials.Certificate(service_account)
            firebase_admin.initialize_app(cred)

        firebase_ready = True
        print("NOVA Firebase Security: READY")

    except Exception as error:
        print("Firebase initialization failed:", error)

else:
    print("Firebase credentials missing. Protected APIs are disabled.")

# -------------------------
# PUBLIC HEALTH CHECK
# -------------------------

@app.get("/")
async def home():
    return {
        "system": "NOVA Ultimate Jarvis",
        "status": "Running",
        "security": "Enabled"
    }

@app.get("/health")
async def health():
    return {
        "backend": "online",
        "firebase": firebase_ready
    }

# -------------------------
# FIREBASE TOKEN VERIFY
# -------------------------

async def verify_user(
    credentials_data: HTTPAuthorizationCredentials = Depends(security)
):
    if not firebase_ready:
        raise HTTPException(
            status_code=503,
            detail="Firebase authentication is not configured"
        )

    if credentials_data is None:
        raise HTTPException(
            status_code=401,
            detail="Authentication required"
        )

    if credentials_data.scheme.lower() != "bearer":
        raise HTTPException(
            status_code=401,
            detail="Bearer token required"
        )

    try:
        decoded_token = auth.verify_id_token(
            credentials_data.credentials,
            check_revoked=True
        )

        return {
            "uid": decoded_token["uid"],
            "email": decoded_token.get("email")
        }

    except Exception:
        raise HTTPException(
            status_code=401,
            detail="Invalid or expired Firebase token"
        )

# -------------------------
# PROTECTED USER API
# -------------------------

@app.get("/api/me")
@limiter.limit("30/minute")
async def get_profile(
    request: Request,
    user: dict = Depends(verify_user)
):
    return {
        "message": "Authentication successful",
        "user": user,
        "agent": "NOVA AI"
    }

# -------------------------
# PROTECTED AGENT API
# -------------------------

@app.post("/api/agent")
@limiter.limit("10/minute")
async def run_agent(
    request: Request,
    user: dict = Depends(verify_user)
):
    return {
        "status": "authenticated",
        "user_id": user["uid"],
        "message": "Agent gateway is ready",
        "note": "AI agent execution will be added next."
    }
