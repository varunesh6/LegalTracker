import React from 'react';

export const Badge = ({ variant = 'primary', children, pulse = false }) => {
  const getBadgeClass = () => {
    switch (variant) {
      case 'success':
      case 'ACTIVE':
      case 'CLOSED':
      case 'APPROVED':
      case 'DISPOSED':
        return 'badge-success';
      case 'warning':
      case 'PENDING':
      case 'SUMMONS_NOTICE':
      case 'APPEARANCE':
      case 'UNDER_REVIEW':
      case 'BUSY':
        return 'badge-warning';
      case 'danger':
      case 'DISMISSED':
      case 'SUSPENDED':
      case 'REJECTED':
      case 'URGENT':
      case 'CRITICAL':
      case 'OVERDUE':
        return 'badge-danger';
      case 'info':
      case 'EVIDENCE':
      case 'ARGUMENTS':
      case 'ON_LEAVE':
        return 'badge-info';
      case 'gold':
      case 'ORDERS_JUDGMENT':
      case 'VERIFIED':
        return 'badge-gold';
      case 'purple':
      case 'LEGAL_AID':
        return 'badge-purple';
      default:
        return 'badge-primary';
    }
  };

  return (
    <span className={`badge ${getBadgeClass()}`}>
      <span className={`badge-dot ${pulse ? 'pulse-dot' : ''}`} />
      <span>{children}</span>
    </span>
  );
};
