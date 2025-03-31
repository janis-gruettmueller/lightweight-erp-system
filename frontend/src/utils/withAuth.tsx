// src/utils/withAuth.ts
"use client";

import { useRouter } from 'next/navigation';
import { useEffect } from 'react';

const withAuth = (WrappedComponent: React.ComponentType) => {
  return function WithAuthComponent(props: any) {
    // Temporarily disabled auth check
    return <WrappedComponent {...props} />;

    // Original code (commented out for now)
    /*
    const router = useRouter();

    useEffect(() => {
      // Check if user is authenticated
      const isAuthenticated = localStorage.getItem('isAuthenticated');
      
      if (!isAuthenticated) {
        router.push('/login');
      }
    }, [router]);

    return <WrappedComponent {...props} />;
    */
  };
};

export default withAuth;