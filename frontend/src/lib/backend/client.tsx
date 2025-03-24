import createClient from "openapi-fetch";
import { paths } from "./apiV1/schema";

const clientWithNoHeaders = createClient<paths>({
  baseUrl: "http://localhost:8080",
});

const client = createClient<paths>({
  baseUrl: "http://localhost:8080",
  headers: {
    "Content-Type": "application/json",
  },
  credentials: "include",
});

export { client, clientWithNoHeaders };
