const path = require('path');
const webpack = require('webpack');
const {CleanWebpackPlugin} = require('clean-webpack-plugin');
const CopyWebpackPlugin = require('copy-webpack-plugin');
const HtmlWebpackPlugin = require('html-webpack-plugin');
const MiniCssExtractPlugin = require('mini-css-extract-plugin');
const VueLoaderPlugin = require('vue-loader/lib/plugin');
const VuetifyLoaderPlugin = require('vuetify-loader/lib/plugin');
const {filter, keys, map, mapValues} = require('lodash');

const CONFIGS = {
  web: {
    entrypoints: {
      index: path.resolve(__dirname, 'src', 'web', 'index.ts'),
    },
    outputPath: path.resolve(__dirname, 'build'),
    copyPatterns: [
      {
        from: path.resolve(__dirname, 'assets', 'icon.ico'),
        to: path.resolve(__dirname, 'build', 'favicon.ico'),
      },
      {
        from: path.resolve(
          __dirname,
          'assets',
          '{icon-192x192.png,icon-512x512.png}'
        ),
        to: path.resolve(__dirname, 'build'),
        toType: 'dir',
      },
      {
        from: path.resolve(__dirname, 'static'),
        to: path.resolve(__dirname, 'build'),
      },
    ],
  },
  overwolf: {
    entrypoints: {
      controller: path.resolve(
        __dirname,
        'src',
        'overwolf',
        'windows',
        'controller',
        'index.ts'
      ),
      results: path.resolve(
        __dirname,
        'src',
        'overwolf',
        'windows',
        'results',
        'index.ts'
      ),
      settings: path.resolve(
        __dirname,
        'src',
        'overwolf',
        'windows',
        'settings',
        'index.ts'
      ),
      username: path.resolve(
        __dirname,
        'src',
        'overwolf',
        'windows',
        'username',
        'index.ts'
      ),
    },
    outputPath: path.resolve(__dirname, 'overwolf', 'build'),
    copyPatterns: [
      {
        from: path.resolve(
          __dirname,
          'assets',
          '{icon.ico,icon-192x192.png,icon-256x256.png,icon-512x512.png,icon-grayscale-256x256.png}'
        ),
        to: path.resolve(__dirname, 'overwolf', 'build'),
        toType: 'dir',
      },
    ],
  },
};

const APIS = {
  local: JSON.stringify('http://localhost:8080/api'),
  external: JSON.stringify('https://api.ironquest.co.uk/api'),
};

const config = CONFIGS[process.env.TARGET] || CONFIGS.web;
const api = APIS[process.env.API] || APIS.external;

function entrypoints(options) {
  return map(config.entrypoints, (entrypoint, name) => {
    return new HtmlWebpackPlugin({
      filename: name + '.html',
      template: path.resolve(__dirname, 'src', 'index.html'),
      excludeChunks: filter(keys(config.entrypoints), key => key !== name),
      ...options,
    });
  });
}

function styleLoaders(extract) {
  const styleLoader = extract
    ? MiniCssExtractPlugin.loader
    : 'vue-style-loader';
  const cssLoader = {
    loader: 'css-loader',
    options: {
      esModule: false,
      sourceMap: true,
    },
  };
  const postCssLoader = {
    loader: 'postcss-loader',
    options: {
      sourceMap: true,
    },
  };

  return [
    {
      test: /\.s[ac]ss$/i,
      use: [
        styleLoader,
        cssLoader,
        postCssLoader,
        {
          loader: 'sass-loader',
          options: {
            implementation: require('sass'),
            sourceMap: true,
            sassOptions: {
              fiber: require('fibers'),
            },
          },
        },
      ],
    },
    {
      test: /\.css$/,
      use: [styleLoader, cssLoader, postCssLoader],
    },
  ];
}

function tsLoader(configFile) {
  return {
    test: /\.tsx?$/,
    loader: 'ts-loader',
    exclude: /node_modules|vue\/src/,
    options: {
      appendTsSuffixTo: [/\.vue$/],
      configFile: configFile,
    },
  };
}

module.exports = {
  entrypoints,
  styleLoaders,
  tsLoader,
  webpack: {
    entry: config.entrypoints,
    module: {
      rules: [
        {
          test: /\.(woff2?|eot|ttf|otf)(\?.*)?$/i,
          use: [
            {
              loader: 'url-loader',
              options: {
                limit: 4096,
                fallback: {
                  loader: 'file-loader',
                  options: {
                    name: 'fonts/[name].[hash:8].[ext]',
                  },
                },
              },
            },
          ],
        },
        {
          test: /\.vue$/,
          loader: 'vue-loader',
        },
      ],
    },
    plugins: [
      new CleanWebpackPlugin(),
      new VueLoaderPlugin(),
      new webpack.DefinePlugin({
        __API__: api,
      }),
      new CopyWebpackPlugin({
        patterns: config.copyPatterns
      }),
      new VuetifyLoaderPlugin(),
    ],
    resolve: {
      alias: {
        vue$: 'vue/dist/vue.esm.js',
        '@': path.resolve(__dirname, 'src'),
      },
      extensions: ['.tsx', '.ts', '.js', '.vue'],
      symlinks: false,
    },
    output: {
      filename: 'js/[name].js',
      path: config.outputPath
    },
    node: {
      setImmediate: false,
      dgram: 'empty',
      fs: 'empty',
      net: 'empty',
      tls: 'empty',
      child_process: 'empty',
    },
  },
};
