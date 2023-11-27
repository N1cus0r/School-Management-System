import React from "react";
import ReactDOM from "react-dom/client";
import { BrowserRouter } from "react-router-dom";
import { ChakraProvider } from "@chakra-ui/react";
import AuthProvider from "./providers/AuthProvider.tsx";
import AppRoutes from "./routing/AppRoutes.tsx";
import { QueryClient, QueryClientProvider } from "react-query";

const queryClient = new QueryClient();

ReactDOM.createRoot(document.getElementById("root")!).render(
  <React.StrictMode>
    <ChakraProvider>
      <BrowserRouter>
        <QueryClientProvider client={queryClient}>
          <AuthProvider>
            <AppRoutes />
          </AuthProvider>
        </QueryClientProvider>
      </BrowserRouter>
    </ChakraProvider>
  </React.StrictMode>,
);
