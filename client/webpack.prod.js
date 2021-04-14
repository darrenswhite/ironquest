const BrotliPlugin = require('brotli-webpack-plugin');
const CompressionPlugin = require('compression-webpack-plugin');
const CssMinimizerPlugin = require('css-minimizer-webpack-plugin');
const MiniCssExtractPlugin = require('mini-css-extract-plugin');
const TerserPlugin = require('terser-webpack-plugin');
const webpack = require('webpack');
const merge = require('webpack-merge');
const {GenerateSW} = require('workbox-webpack-plugin');

const common = require('./webpack.common.js');

module.exports = merge.mergeWithCustomize({
  customizeArray: merge.customizeArray({
    'entry.*': 'append',
  }),
  customizeObject: merge.customizeObject({
    entry: 'append',
  }),
})(common.webpack, {
  devtool: 'source-map',
  mode: 'production',
  module: {
    rules: [
      common.tsLoader('tsconfig.prod.json'),
      ...common.styleLoaders(true),
    ],
  },
  optimization: {
    mangleWasmImports: true,
    mergeDuplicateChunks: true,
    minimize: true,
    minimizer: [
      new TerserPlugin({
        parallel: true,
        terserOptions: {
          warnings: false,
        },
      }),
      new CssMinimizerPlugin(),
    ],
    moduleIds: 'deterministic',
    removeAvailableModules: true,
    removeEmptyChunks: true,
    runtimeChunk: 'single',
    // sideEffects: false,
    splitChunks: {
      chunks: 'all',
      maxInitialRequests: Infinity,
      minSize: 0,
      cacheGroups: {
        vendor: {
          test: /[\\/]node_modules[\\/]/,
          name(module) {
            const packageName = module.context.match(
              /[\\/]node_modules[\\/](.*?)([\\/]|$)/
            )[1];

            return `vendor.${packageName.replace('@', '')}`;
          },
        },
      },
    },
  },
  output: {
    filename: 'js/[name].[chunkhash].js',
  },
  plugins: [
    ...common.entrypoints({
      inject: true,
      minify: {
        removeComments: true,
        collapseWhitespace: true,
        removeAttributeQuotes: true,
      },
    }),
    new MiniCssExtractPlugin({
      filename: 'css/[name].[chunkhash].css',
    }),
    new CompressionPlugin({
      algorithm: 'gzip',
      filename: '[path][base].gz[query]',
      minRatio: 0.8,
      test: /\.(js|css|html|svg)$/i,
      threshold: 10240,
    }),
    new BrotliPlugin({
      asset: '[path].br[query]',
      minRatio: 0.8,
      test: /\.(js|css|html|svg)$/i,
      threshold: 10240,
    }),
    new GenerateSW({
      cacheId: 'ironquest',
      clientsClaim: true,
      dontCacheBustURLsMatching: /\.\w{8}\./,
      modifyURLPrefix: {
        'build/': '',
      },
      skipWaiting: true,
      swDest: 'service-worker.js',
    }),
  ],
});
