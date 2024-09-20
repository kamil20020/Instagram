import React from 'react';
import { useState, useEffect, useRef } from 'react';

const useOutsideClick = (callback: () => void) => {
    const ref = React.useRef<HTMLInputElement>();
  
    React.useEffect(() => {
      const handleClick = (event: any) => {
        if (ref.current && !ref.current.contains(event.target)) {
          callback();
        }
      };
  
      document.addEventListener('click', handleClick, true);
  
      return () => {
        document.removeEventListener('click', handleClick, true);
      };
    }, [ref]);
  
    return ref;
};

export default useOutsideClick;