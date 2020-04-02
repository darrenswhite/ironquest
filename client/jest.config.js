module.exports = {
  collectCoverageFrom: ['src/**/*.{ts,vue}', '!**/*.d.ts'],
  coverageThreshold: {
    global: {
      branches: 80,
      functions: 80,
      lines: 80,
      statements: 80,
    },
  },
  globals: {
    'ts-jest': {
      babelConfig: false,
      diagnostics: false,
      tsConfig: './tsconfig.json',
    },
    'vue-jest': {
      babelConfig: false,
    },
  },
  moduleDirectories: ['node_modules'],
  moduleFileExtensions: ['js', 'ts', 'vue'],
  moduleNameMapper: {
    '^@/(.*)$': '<rootDir>/src/$1',
    '\\.(css|sass|scss)$': 'identity-obj-proxy',
  },
  setupFiles: ['<rootDir>/jest.setup.ts'],
  snapshotSerializers: ['jest-serializer-html'],
  testMatch: ['<rootDir>/test/**/*.test.ts'],
  transform: {
    '\\.(sass|scss)$': 'jest-css-modules',
    '^.+\\.(j|t)s$': 'ts-jest',
    '.*\\.(vue)$': 'vue-jest',
  },
  transformIgnorePatterns: ['node_modules/(?!vue-router)'],
  verbose: false,
};
