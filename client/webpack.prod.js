const merge = require('webpack-merge');
const webpack = require('webpack');
const BrotliPlugin = require('brotli-webpack-plugin');
const BundleAnalyzerPlugin = require('webpack-bundle-analyzer')
  .BundleAnalyzerPlugin;
const CompressionPlugin = require('compression-webpack-plugin');
const MiniCssExtractPlugin = require('mini-css-extract-plugin');
const OptimizeCSSAssetsPlugin = require('optimize-css-assets-webpack-plugin');
const SWPrecacheWebpackPlugin = require('sw-precache-webpack-plugin');
const TerserPlugin = require('terser-webpack-plugin');
const common = require('./webpack.common.js');

module.exports = merge.mergeWithCustomize({
  customizeArray: merge.customizeArray({
    'entry.*': 'append'
  }),
  customizeObject: merge.customizeObject({
    entry: 'append'
  })
})(common.webpack, {
  mode: 'production',
  optimization: {
    mangleWasmImports: true,
    mergeDuplicateChunks: true,
    minimize: true,
    minimizer: [
      new TerserPlugin({
        parallel: true,
        sourceMap: true,
        terserOptions: {
          warnings: false,
        },
      }),
      new OptimizeCSSAssetsPlugin({
        cssProcessorOptions: {
          safe: true,
          map: {
            inline: false,
          },
        },
      }),
    ],
    removeAvailableModules: true,
    removeEmptyChunks: true,
    runtimeChunk: 'single',
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
  devtool: 'source-map',
  module: {
    rules: [
      common.tsLoader('tsconfig.prod.json'),
      ...common.styleLoaders(true),
    ],
  },
  output: {
    filename: 'js/[name].[chunkhash].js',
  },
  plugins: [
    new webpack.HashedModuleIdsPlugin(),
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
    new webpack.optimize.ModuleConcatenationPlugin(),
    new CompressionPlugin({
      algorithm: 'gzip',
      filename: '[path].gz[query]',
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
    new SWPrecacheWebpackPlugin({
      cacheId: 'ironquest',
      dontCacheBustUrlsMatching: /\.\w{8}\./,
      filename: 'service-worker.js',
      minify: true,
      staticFileGlobs: [
        'build/**/*.{js,css,png,txt,map,html}',
        'index.html',
        'manifest.json',
      ],
      staticFileGlobsIgnorePatterns: [/\.map$/],
      stripPrefix: 'build/',
    }),
  ],
});
