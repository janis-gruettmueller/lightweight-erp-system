// src/utils/withAuth.ts
"use client";

import { useRouter } from 'next/navigation';
import { useEffect, useState } from 'react';
import { ComponentType } from 'react';

interface AuthProps {}

function withAuth<P extends object>(WrappedComponent: ComponentType<P>) {
  return (props: P & AuthProps) => {
    const router = useRouter();
    const [isAuthenticated, setIsAuthenticated] = useState<boolean | null>(null);

    useEffect(() => {
      const checkAuth = async () => {
        try {
          const response = await fetch('/api/auth/session');
          if (response.ok) {
            setIsAuthenticated(true);
          } else {
            setIsAuthenticated(false);
            router.push('/login');
          }
        } catch (error) {
          console.error("Error checking auth:", error);
          setIsAuthenticated(false);
          router.push('/login');
        }
      };

      checkAuth();
    }, [router]);

    if (isAuthenticated === false) {
      return null;
    }

    return <WrappedComponent {...props} />;
  };
}

export default withAuth;