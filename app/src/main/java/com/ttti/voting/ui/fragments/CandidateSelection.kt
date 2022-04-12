package com.ttti.voting.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.ttti.voting.R
import com.ttti.voting.ui.holder.AndroidTreeView
import com.ttti.voting.ui.holder.IconTreeItemHolder
import com.ttti.voting.ui.holder.TreeNode


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [CandidateSelection.newInstance] factory method to
 * create an instance of this fragment.
 */
class CandidateSelection : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private var statusBar: TextView? = null
    private var tView: AndroidTreeView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }
    private var counter = 0
    private fun fillDownloadsFolder(node: TreeNode) {
        val downloads = TreeNode(IconTreeItemHolder.IconTreeItem(R.string.ic_folder, "Downloads" + counter++))
        node.addChild(downloads)
        if (counter < 5) {
            fillDownloadsFolder(downloads)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView: View = inflater.inflate(R.layout.fragment_candidate_selection, null, false)

        val containerView = rootView.findViewById<View>(R.id.container) as ViewGroup
       // statusBar = rootView.findViewById<View>(R.id.status_bar) as TextView


        val root = TreeNode.root()
        val mainRoot = TreeNode(IconTreeItemHolder.IconTreeItem(R.string.ic_person, "Candidate Selection"))

        val category1Root = TreeNode(IconTreeItemHolder.IconTreeItem(R.string.ic_person, "President"))
        val category2Root = TreeNode(IconTreeItemHolder.IconTreeItem(R.string.ic_person, "Governor"))
        val category3Root = TreeNode(IconTreeItemHolder.IconTreeItem(R.string.ic_person, "Senator"))
        val category4Root = TreeNode(IconTreeItemHolder.IconTreeItem(R.string.ic_person, "MCA"))

        val candidate1 = TreeNode(IconTreeItemHolder.IconTreeItem(R.string.ic_thumbs_down, "President Candidate 1"))
        val candidate2 = TreeNode(IconTreeItemHolder.IconTreeItem(R.string.ic_thumbs_down, "President Candidate 2"))
        category1Root.addChildren(candidate1,candidate2)


        val candidate3 = TreeNode(IconTreeItemHolder.IconTreeItem(R.string.ic_thumbs_down, "Governor 1"))
        val candidate4 = TreeNode(IconTreeItemHolder.IconTreeItem(R.string.ic_thumbs_down, "Governor 2"))
        val candidate5 = TreeNode(IconTreeItemHolder.IconTreeItem(R.string.ic_thumbs_down, "Governor 3"))
        category2Root.addChildren(candidate3,candidate4,candidate5)

        /*val myDocuments = TreeNode(IconTreeItemHolder.IconTreeItem(R.string.ic_person, "Governor Category"))
        val downloads = TreeNode(IconTreeItemHolder.IconTreeItem(R.string.ic_person, "Senator"))
        val file1 = TreeNode(IconTreeItemHolder.IconTreeItem(R.string.ic_person, "MCA"))
        //fillDownloadsFolder(downloads)
        downloads.addChildren(file1)
*/
        //myDocuments.addChild(downloads)
        //mainRoot.addChildren(category1Root,category2Root,category3Root,category4Root)
        root.addChildren(category1Root,category2Root,category3Root,category4Root)

        tView = AndroidTreeView(activity, root)
        tView!!.setDefaultAnimation(true)
        tView!!.setDefaultContainerStyle(R.style.TreeNodeStyleCustom)
        tView!!.setDefaultViewHolder(IconTreeItemHolder::class.java)
        containerView.addView(tView!!.view)
        tView?.expandAll()



        // Inflate the layout for this fragment
        return rootView
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment CandidateSelection.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            CandidateSelection().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}