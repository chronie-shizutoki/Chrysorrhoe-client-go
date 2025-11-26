import React from 'react';
import { useTranslation } from 'react-i18next';
import '../styles/ChangeType.css'

const ChangeType = () => {
  const { t } = useTranslation();
  return (
    <a href="http://192.168.0.197:3100" className="ChangeType">
      {t('changeToNormalMode')}
    </a>
  );
};

export default ChangeType;
