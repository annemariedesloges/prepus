import React from 'react'
import { Component } from 'react'
import { connect } from 'react-redux'
import { bindActionCreators } from 'redux'

import { FileActions } from '../redux/modules/file'
import FileExpanded from './file-expanded'
import File from './file'

class FilesRight extends Component {
  render () {
    var styles = {
      alignment: {
        textAlign: 'left'
      },
      borders: {
        border: 1,
        borderStyle: 'solid',
        borderColor: 'gray',
        borderRadius: 5,
        marginTop: 20
      }
    }

    let leftFileSimilarities = []

    if (this.props.activeFileLeft === -1 &&
        this.props.activeFileLeft !== -1 &&
        this.props.activeFileRight === -1) {
      leftFileSimilarities = this.props.files.filter(file => file.id === this.props.activeFileLeft)[0].similarities

      let rightFilesIds = leftFileSimilarities.map(file => file.id)
      rightFilesIds = rightFilesIds.filter((file, pos) => (rightFilesIds.indexOf(file) === pos))

      if (leftFileSimilarities.length === 0) {
        return (<div className="col-lg-5 col-lg-offset-1" style={styles.borders}> Aucun plagiat détecté </div>)
      } else {
        return (
          <div className="col-lg-5 col-lg-offset-1" style={styles.borders}>
            <ul className="list-unstyled" style={styles.alignment}>
              {this.props.files.filter(
                file => rightFilesIds === file.id).map(
                    file => <File file={file} key={file.id} setActiveFile={this.props.setActiveFileRight} />
                )
              }
            </ul>
          </div>
        )
      }
    } else if (this.props.activeFileRight !== -1) {
      return (
        <div className="col-lg-5 col-lg-offset-1" style={styles.borders}>
          <button onClick={this.props.removeActiveFileRight} style={{ float: 'right' }}>X</button>
          <ul className="list-unstyled" style={styles.alignment}>
            {this.props.files.filter(
                file => file.id === this.props.activeFileRight).map((file) => <FileExpanded file={file} key={file.id} />
            )}
          </ul>
        </div>
      )
    }
    return (<div></div>)
  }
}
function mapStateToProps (state) {
  var currentDepot = state.fileReducer.depots.filter(depot => depot.id === state.fileReducer.activeDepot)[0]
  var currentFiles
  if (currentDepot !== undefined) {
    currentFiles = currentDepot.files
  }
  return {
    files: currentFiles,
    activeFileLeft: state.fileReducer.activeFileLeft,
    activeFileRight: state.fileReducer.activeFileRight,
    similarities: state.fileReducer.similarities
  }
}

function mapDispatchToProps (dispatch) {
  return bindActionCreators({
    setActiveFileRight: FileActions.setActiveFileRight,
    removeActiveFileRight: FileActions.removeActiveFileRight,
    removeActiveFiles: FileActions.removeActiveFiles
  }, dispatch)
}

export default connect(mapStateToProps, mapDispatchToProps)(FilesRight)
